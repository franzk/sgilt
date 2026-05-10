/**
 * Couche API — appels HTTP vers /events
 */
import { apiFetch } from '~/composables/useApi'
import type { EvenementSummaryDto } from '../dto/EvenementSummaryDto'
import type { EventDetailDto, EventPatchRequestDto } from '../dto/EventDetailDto'
import type { EventCountsDto } from '../dto/EventCountsDto'
import type { EventReservationSummaryDto } from '../dto/EventReservationSummaryDto'

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
