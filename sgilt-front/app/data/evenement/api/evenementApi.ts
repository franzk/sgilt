/**
 * Couche API — appels HTTP vers /user/events
 */
import { apiFetch } from '~/composables/useApi'
import type { DemandeRequest } from '~/types/demande'
import type { EvenementSummaryDto } from '../dto/EvenementSummaryDto'
import type { EventDetailDto, EventPatchRequestDto } from '../dto/EventDetailDto'
import type { EventCountsDto } from '../dto/EventCountsDto'
import type { JournalEvenementPageDto } from '../dto/JournalEvenementDto'

export interface CoverUrlResponseDto {
  imagePath: string
}

export async function getEvenementsApi(): Promise<EvenementSummaryDto[]> {
  return apiFetch<EvenementSummaryDto[]>('/user/events')
}

export async function getEventDetailApi(eventId: string): Promise<EventDetailDto> {
  return apiFetch<EventDetailDto>(`/user/events/${eventId}`)
}

export async function patchEventApi(eventId: string, patch: EventPatchRequestDto): Promise<EventDetailDto> {
  return apiFetch<EventDetailDto>(`/user/events/${eventId}`, { method: 'PATCH', body: patch })
}

export async function getEventCountsApi(eventId: string): Promise<EventCountsDto> {
  return apiFetch<EventCountsDto>(`/user/events/${eventId}/counts`)
}

export async function getEventJournalApi(eventId: string, page: number): Promise<JournalEvenementPageDto> {
  return apiFetch<JournalEvenementPageDto>(`/user/events/${eventId}/journal`, { params: { page } })
}

export async function uploadEventCoverApi(eventId: string, file: File): Promise<CoverUrlResponseDto> {
  const body = new FormData()
  body.append('file', file)
  return apiFetch<CoverUrlResponseDto>(`/user/events/${eventId}/cover`, { method: 'PATCH', body })
}

export async function selectEventCoverApi(eventId: string, imagePath: string): Promise<CoverUrlResponseDto> {
  return apiFetch<CoverUrlResponseDto>(`/user/events/${eventId}/cover/select`, {
    method: 'PATCH',
    body: { imagePath },
  })
}

export async function createEventApi(body: DemandeRequest): Promise<{ eventId: string }> {
  return apiFetch<{ eventId: string }>('/user/events', { method: 'POST', body })
}

export async function addReservationApi(eventId: string, prestataireId: string, message: string | null): Promise<void> {
  await apiFetch(`/user/events/${eventId}/reservations`, {
    method: 'POST',
    body: { prestataireId, message },
  })
}
