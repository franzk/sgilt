import { apiFetch } from '~/composables/useApi'
import { getFeedApi, addNoteApi, uploadDocumentApi, deleteDocumentApi } from '../api/reservationFeedApi'
import type { FeedItem } from '../domain/FeedItem'
import type { FeedItemDto } from '../dto/FeedItemDto'

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
    name: dto.name ?? null,
    fileType: dto.fileType ?? null,
    url: dto.url ?? null,
    generatedKey: dto.generatedKey ?? null,
  }
}

export async function fetchFeed(reservationId: string): Promise<FeedItem[]> {
  const items = await getFeedApi(reservationId)
  return items.map(mapItem)
}

export async function uploadDocument(
  reservationId: string,
  file: File,
  isPersonal: boolean,
): Promise<FeedItem> {
  const dto = await uploadDocumentApi(reservationId, file, isPersonal)
  return mapItem(dto)
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
