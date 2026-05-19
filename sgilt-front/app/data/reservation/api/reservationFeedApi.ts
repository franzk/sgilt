import { apiFetch } from '~/composables/useApi'
import type { FeedItemDto } from '../dto/FeedItemDto'

export async function getFeedApi(reservationId: string): Promise<FeedItemDto[]> {
  return apiFetch<FeedItemDto[]>(`/reservations/${reservationId}/feed`)
}

export async function uploadDocumentApi(
  reservationId: string,
  file: File,
  isPersonal: boolean,
): Promise<FeedItemDto> {
  const body = new FormData()
  body.append('file', file)
  body.append('isPersonal', String(isPersonal))
  return apiFetch<FeedItemDto>(`/reservations/${reservationId}/feed/documents`, {
    method: 'POST',
    body,
  })
}

export async function addNoteApi(
  reservationId: string,
  title: string,
  content: string,
  isPersonal: boolean,
): Promise<FeedItemDto> {
  return apiFetch<FeedItemDto>(`/reservations/${reservationId}/feed/notes`, {
    method: 'POST',
    body: { title, content, isPersonal },
  })
}
