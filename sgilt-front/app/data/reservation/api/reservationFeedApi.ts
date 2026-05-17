import { apiFetch } from '~/composables/useApi'
import type { FeedItemDto } from '../dto/FeedItemDto'

export async function getFeedApi(reservationId: string): Promise<FeedItemDto[]> {
  return apiFetch<FeedItemDto[]>(`/reservations/${reservationId}/feed`)
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
