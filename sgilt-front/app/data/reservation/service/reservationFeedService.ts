/**
 * Service du fil d'une réservation (frise du Suivi) : transforme les DTO en domaine (un document
 * devient un envoi et ses fichiers), envoie les documents fichier par fichier, ajoute les notes,
 * supprime et télécharge les documents. Utilisé par useReservationFeed.
 */
import { apiFetch } from '~/composables/useApi'
import {
  getFeedApi,
  addNoteApi,
  uploadDocumentApi,
  deleteDocumentApi,
} from '../api/reservationFeedApi'
import type { FeedFile } from '../domain/FeedFile'
import type { FeedItem } from '../domain/FeedItem'
import type { FeedItemDto } from '../dto/FeedItemDto'

function mapFiles(dto: FeedItemDto): FeedFile[] | null {
  if (dto.files) {
    return dto.files.map((file) => ({
      id: file.id,
      name: file.name,
      fileType: file.fileType ?? null,
      url: file.url,
    }))
  }
  // TODO brique back : le back actuel renvoie un document seul, à plat ; on en fait un envoi d'un
  // fichier. À retirer quand le fil renverra `files`.
  if (dto.type === 'document') {
    return [{ id: dto.id, name: dto.name ?? '', fileType: dto.fileType ?? null, url: dto.url ?? '' }]
  }
  return null
}

function mapItem(dto: FeedItemDto): FeedItem {
  return {
    type: dto.type,
    id: dto.id,
    title: dto.title ?? '',
    author: {
      id: dto.authorId,
      name: dto.authorName,
      photo: dto.authorPhoto ?? undefined,
      role: dto.authorRole,
    },
    createdAt: new Date(dto.createdAt),
    content: dto.content ?? null,
    isPersonal: dto.isPersonal ?? null,
    isMessageInitial: dto.isMessageInitial ?? null,
    files: mapFiles(dto),
    generatedKey: dto.generatedKey ?? null,
  }
}

export async function fetchFeed(reservationId: string): Promise<FeedItem[]> {
  const items = await getFeedApi(reservationId)
  return items.map(mapItem)
}

// Envoi de plusieurs fichiers : un appel par fichier, l'un après l'autre. Un échec n'arrête pas les
// suivants : les fichiers passés forment l'envoi (null si aucun), ceux qui ont échoué sont rendus par
// leur nom.
// TODO brique back : le back ne regroupe pas encore ; on assemble ici les réponses en un seul envoi.
// À reprendre avec l'uploadId envoyé avec chaque fichier et l'appel de clôture ; l'id de l'envoi
// (aujourd'hui celui de son premier fichier) deviendra l'uploadId.
export async function uploadDocuments(
  reservationId: string,
  files: File[],
  isPersonal: boolean,
): Promise<{ item: FeedItem | null; failedFiles: string[] }> {
  const items: FeedItem[] = []
  const failedFiles: string[] = []
  for (const file of files) {
    try {
      items.push(mapItem(await uploadDocumentApi(reservationId, file, isPersonal)))
    } catch (e) {
      console.error(e)
      failedFiles.push(file.name)
    }
  }
  const first = items[0]
  const item = first ? { ...first, files: items.flatMap((i) => i.files ?? []) } : null
  return { item, failedFiles }
}

export async function deleteDocument(reservationId: string, documentId: string): Promise<void> {
  await deleteDocumentApi(reservationId, documentId)
}

export async function downloadDocument(url: string, fileName: string): Promise<void> {
  const blob = await apiFetch<Blob>(url, { responseType: 'blob' })
  const objectUrl = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = objectUrl
  a.download = fileName
  a.click()
  URL.revokeObjectURL(objectUrl)
}

export async function addNote(
  reservationId: string,
  title: string,
  content: string,
  isPersonal: boolean,
): Promise<FeedItem> {
  const dto = await addNoteApi(reservationId, title, content, isPersonal)
  return mapItem(dto)
}
