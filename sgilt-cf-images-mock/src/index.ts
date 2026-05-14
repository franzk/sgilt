import express, { Request, Response } from 'express'
import busboy from 'busboy'
import path from 'path'
import fs from 'fs'
import { v4 as uuidv4 } from 'uuid'
import { DatabaseSync } from 'node:sqlite'

const PORT        = process.env.PORT        ?? 5029
const STORAGE_DIR = process.env.STORAGE_DIR ?? path.join(__dirname, '..', 'storage')
const DB_PATH     = process.env.DB_PATH     ?? path.join(__dirname, '..', 'mock.db')

// ── Types ─────────────────────────────────────────────────────────────────────

interface ImageRow {
  filename: string
}

interface UploadResult {
  id: string
  filename: string
  uploaded: string
  requireSignedURLs: boolean
  variants: string[]
}

interface CfResponse<T> {
  result: T
  success: boolean
  errors: { message: string }[]
  messages: string[]
}

// ── Storage ───────────────────────────────────────────────────────────────────

fs.mkdirSync(path.join(STORAGE_DIR, 'bank'), { recursive: true })

// ── SQLite ────────────────────────────────────────────────────────────────────

const db = new DatabaseSync(DB_PATH)
db.exec(`
  CREATE TABLE IF NOT EXISTS images (
    id          TEXT PRIMARY KEY,
    filename    TEXT NOT NULL,
    uploaded_at TEXT NOT NULL
  )
`)

/**
 * Enregistre en base les images pré-committées dans `storage/bank/`.
 * Appelée au démarrage — utilise INSERT OR IGNORE pour être idempotente.
 * L'imageId d'une image de banque suit le format `bank/<nom-sans-extension>`.
 */
function registerBankImages(): void {
  const bankDir = path.join(STORAGE_DIR, 'bank')
  if (!fs.existsSync(bankDir)) return

  const stmt = db.prepare(
    'INSERT OR IGNORE INTO images (id, filename, uploaded_at) VALUES (?, ?, ?)',
  )
  for (const file of fs.readdirSync(bankDir)) {
    if (file.startsWith('.')) continue
    const imageId = 'bank/' + path.parse(file).name
    stmt.run(imageId, 'bank/' + file, new Date().toISOString())
  }
}

registerBankImages()

// ── App ───────────────────────────────────────────────────────────────────────

const app = express()
app.use(express.json())

/**
 * Reçoit un fichier image, le persiste sur disque et l'enregistre en base.
 *
 * @route POST /images/v1
 * @returns La réponse CF Images avec l'`id` attribué à l'image.
 */
app.post('/images/v1', (req: Request, res: Response) => {
  const bb = busboy({ headers: req.headers })
  let savedFilename: string | null = null
  let originalFilename: string = 'upload'

  bb.on('file', (fieldname, fileStream, info) => {
    originalFilename = info.filename || 'upload'
    const ext = path.extname(originalFilename) || ''
    savedFilename = uuidv4() + ext
    const dest = path.join(STORAGE_DIR, savedFilename)
    fileStream.pipe(fs.createWriteStream(dest))
  })

  bb.on('finish', () => {
    if (!savedFilename) {
      res.status(400).json({ success: false, errors: [{ message: 'No file uploaded' }] })
      return
    }

    console.log(`  saved: ${savedFilename} (${originalFilename})`)

    const imageId = uuidv4()
    db.prepare('INSERT INTO images (id, filename, uploaded_at) VALUES (?, ?, ?)')
      .run(imageId, savedFilename, new Date().toISOString())

    const body: CfResponse<UploadResult> = {
      result: {
        id: imageId,
        filename: originalFilename,
        uploaded: new Date().toISOString(),
        requireSignedURLs: false,
        variants: [],
      },
      success: true,
      errors: [],
      messages: [],
    }
    res.json(body)
  })

  bb.on('error', (err: Error) => {
    console.error('busboy error:', err)
    res.status(500).json({ success: false, errors: [{ message: err.message }] })
  })

  req.pipe(bb as unknown as NodeJS.WritableStream)
})

/**
 * Supprime une image du disque et de la base par son `imageId`.
 * L'imageId peut contenir des slashes (ex. `bank/mariage`).
 *
 * @route DELETE /images/v1/:imageId
 */
app.delete('/images/v1/*', (req: Request, res: Response) => {
  const imageId = req.params[0]
  const row = db.prepare('SELECT filename FROM images WHERE id = ?').get(imageId) as ImageRow | undefined

  if (!row) {
    res.status(404).json({ success: false, errors: [{ message: 'Image not found' }] })
    return
  }

  const filepath = path.join(STORAGE_DIR, row.filename)
  if (fs.existsSync(filepath)) fs.unlinkSync(filepath)
  db.prepare('DELETE FROM images WHERE id = ?').run(imageId)

  res.json({ result: {}, success: true, errors: [], messages: [] })
})

/**
 * Sert une image par son `imageId` et son variant.
 * Format d'URL : `/{imageId}/{variant}` — l'imageId peut contenir des slashes.
 *
 * @route GET /*
 */
app.get('*', (req: Request, res: Response) => {
  const urlPath = req.path

  const lastSlash = urlPath.lastIndexOf('/')
  if (lastSlash <= 0) {
    res.status(404).send('Not found')
    return
  }

  const imageId = urlPath.slice(1, lastSlash)
  const row = db.prepare('SELECT filename FROM images WHERE id = ?').get(imageId) as ImageRow | undefined

  if (!row) {
    res.status(404).send('Image not found')
    return
  }

  const filepath = path.resolve(path.join(STORAGE_DIR, row.filename))
  if (!fs.existsSync(filepath)) {
    res.status(404).send('File not found')
    return
  }

  res.sendFile(filepath)
})

app.listen(PORT, () => {
  console.log(`sgilt-cf-images-mock listening on http://localhost:${PORT}`)
})
