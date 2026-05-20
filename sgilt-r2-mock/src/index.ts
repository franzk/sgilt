/**
 * Mock R2 (S3-compatible) pour le développement local.
 *
 * Expose deux interfaces sur le même port :
 *
 * 1. API S3 path-style (appelée par sgilt-core via le AWS SDK) :
 *    PUT    /{bucket}/{key}  — reçoit le binaire brut, sauvegarde dans storage/{key}
 *    DELETE /{bucket}/{key}  — supprime storage/{key}
 *
 * 2. Delivery (appelée par le front via NUXT_PUBLIC_IMAGE_BASE_URL) :
 *    GET    /{key}           — sert storage/{key} avec res.sendFile (Content-Type auto)
 *
 * Les images de banque (bank/mariage.jpg, etc.) sont des fichiers committés dans
 * storage/bank/ et servis directement via le même GET.
 *
 * Stack :
 *   express  — routeur HTTP, gère PUT/DELETE/GET et le streaming de fichiers (sendFile)
 *   node:fs  — lecture/écriture/suppression des fichiers sur disque
 *   node:path — construction et validation des chemins (protection contre le path traversal)
 */
import express, { Request, Response } from 'express'
import path from 'node:path'
import fs from 'node:fs'

const PORT        = process.env.PORT        ?? 5029
const STORAGE_DIR = process.env.STORAGE_DIR ?? path.join(__dirname, '..', 'storage')

fs.mkdirSync(path.join(STORAGE_DIR, 'bank'), { recursive: true })
fs.mkdirSync(path.join(STORAGE_DIR, 'uploads'), { recursive: true })

const app = express()

/**
 * Résout un chemin de fichier depuis une clé R2 et vérifie qu'il reste dans STORAGE_DIR.
 * Retourne null si le chemin est en dehors du répertoire de stockage (path traversal).
 */
function resolveStoragePath(key: string): string | null {
  const resolved = path.resolve(path.join(STORAGE_DIR, key))
  return resolved.startsWith(path.resolve(STORAGE_DIR)) ? resolved : null
}

/**
 * S3 PutObject — reçoit le fichier en corps binaire brut et le sauvegarde.
 * L'URL suit le format S3 path-style : `PUT /{bucket}/{key}`.
 *
 * @route PUT /:bucket/*
 */
app.put('/:bucket/*', (req: Request, res: Response) => {
  const key = req.params[0]
  const filepath = resolveStoragePath(key)

  if (!filepath) {
    res.status(400).send('Invalid path')
    return
  }

  fs.mkdirSync(path.dirname(filepath), { recursive: true })
  const stream = fs.createWriteStream(filepath)

  req.pipe(stream)
  stream.on('finish', () => {
    console.log(`PUT ${key}`)
    res.status(200).send()
  })
  stream.on('error', (err) => {
    console.error(`PUT ${key} error:`, err)
    res.status(500).send()
  })
})

/**
 * S3 DeleteObject — supprime le fichier correspondant à la clé.
 * L'URL suit le format S3 path-style : `DELETE /{bucket}/{key}`.
 *
 * @route DELETE /:bucket/*
 */
app.delete('/:bucket/*', (req: Request, res: Response) => {
  const key = req.params[0]
  const filepath = resolveStoragePath(key)

  if (!filepath) {
    res.status(400).send('Invalid path')
    return
  }

  if (fs.existsSync(filepath)) {
    fs.unlinkSync(filepath)
    console.log(`DELETE ${key}`)
  }
  res.status(204).send()
})

/**
 * GET — gère à la fois :
 *   - l'API S3 path-style `GET /{bucket}/{key}` (appelée par sgilt-core)
 *   - la livraison sans bucket `GET /{key}` (appelée par le front)
 *
 * Tente d'abord le chemin complet, puis strip le premier segment (bucket) si le fichier est introuvable.
 *
 * @route GET /*
 */
app.get('*', (req: Request, res: Response) => {
  const fullPath = req.path.slice(1)

  if (!fullPath) {
    res.status(404).send('Not found')
    return
  }

  // Tentative 1 : chemin direct (livraison sans bucket)
  let filepath = resolveStoragePath(fullPath)
  if (filepath && fs.existsSync(filepath)) {
    console.log(`GET (delivery) ${fullPath}`)
    res.sendFile(filepath)
    return
  }

  // Tentative 2 : strip du premier segment (bucket) — format S3 path-style
  const withoutBucket = fullPath.replace(/^[^/]+\//, '')
  if (withoutBucket !== fullPath) {
    filepath = resolveStoragePath(withoutBucket)
    if (filepath && fs.existsSync(filepath)) {
      console.log(`GET (S3) ${withoutBucket}`)
      res.sendFile(filepath)
      return
    }
  }

  console.log(`GET 404: ${fullPath}`)
  res.status(404).send('Not found')
})

app.listen(PORT, () => {
  console.log(`sgilt-r2-mock listening on http://localhost:${PORT}`)
})
