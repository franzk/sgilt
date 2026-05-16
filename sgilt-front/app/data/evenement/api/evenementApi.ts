/**
 * Couche API — appels HTTP vers /events
 */
import { apiFetch } from '~/composables/useApi'
import type { EvenementSummaryDto } from '../dto/EvenementSummaryDto'
import type { EventDetailDto, EventPatchRequestDto } from '../dto/EventDetailDto'
import type { EventCountsDto } from '../dto/EventCountsDto'
import type { EventReservationSummaryDto } from '../dto/EventReservationSummaryDto'
import type { JournalEvenementPageDto } from '../dto/JournalEvenementDto'

export interface CoverUrlResponseDto {
  imagePath: string
}

export async function getEvenementsApi(): Promise<EvenementSummaryDto[]> {
  return apiFetch<EvenementSummaryDto[]>('/events')
}

export async function getEventDetail(eventId: string): Promise<EventDetailDto> {
  return apiFetch<EventDetailDto>(`/events/${eventId}`)
}

export async function patchEventApi(eventId: string, patch: EventPatchRequestDto): Promise<EventDetailDto> {
  return apiFetch<EventDetailDto>(`/events/${eventId}`, { method: 'PATCH', body: patch })
}

export async function getEventCounts(eventId: string): Promise<EventCountsDto> {
  return apiFetch<EventCountsDto>(`/events/${eventId}/counts`)
}

export async function getEventReservations(eventId: string): Promise<EventReservationSummaryDto[]> {
  return apiFetch<EventReservationSummaryDto[]>(`/events/${eventId}/reservations`)
}

export async function getEventJournal(eventId: string, page: number): Promise<JournalEvenementPageDto> {
  return apiFetch<JournalEvenementPageDto>(`/events/${eventId}/journal`, { params: { page } })
}

export async function uploadEventCoverApi(eventId: string, file: File): Promise<CoverUrlResponseDto> {
  const body = new FormData()
  body.append('file', file)
  return apiFetch<CoverUrlResponseDto>(`/events/${eventId}/cover`, { method: 'PATCH', body })
}

export async function selectEventCoverApi(eventId: string, imagePath: string): Promise<CoverUrlResponseDto> {
  return apiFetch<CoverUrlResponseDto>(`/events/${eventId}/cover/select`, {
    method: 'PATCH',
    body: { imagePath },
  })
}

export async function addReservationApi(eventId: string, prestataireId: string, message: string | null): Promise<void> {
  await apiFetch(`/events/${eventId}/reservations`, {
    method: 'POST',
    body: { prestataireId, message },
  })
}
