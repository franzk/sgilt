import { apiFetch } from '~/composables/useApi'
import type { ActiveReservationsDto } from '../dto/ActiveReservationsDto'
import type { ReservationMetaDto } from '../dto/ReservationMetaDto'
import type { EventReservationSummaryDto } from '~/data/evenement/dto/EventReservationSummaryDto'

export async function getReservationsByEventApi(
  eventId: string,
): Promise<EventReservationSummaryDto[]> {
  return apiFetch<EventReservationSummaryDto[]>(`/user/reservations`, { params: { eventId } })
}

export async function getReservationMetaApi(reservationId: string): Promise<ReservationMetaDto> {
  return apiFetch<ReservationMetaDto>(`/user/reservations/${reservationId}`)
}

export async function cancelReservationApi(
  reservationId: string,
  reason?: string,
  isPersonal?: boolean,
): Promise<void> {
  await apiFetch(`/user/reservations/${reservationId}/cancel`, {
    method: 'POST',
    body: { reason: reason ?? null, isPersonal: isPersonal ?? false },
  })
}

export async function markContactedApi(reservationId: string): Promise<void> {
  await apiFetch(`/user/reservations/${reservationId}/mark-contacted`, { method: 'POST' })
}

export async function confirmReservationApi(reservationId: string): Promise<void> {
  await apiFetch(`/user/reservations/${reservationId}/confirm`, { method: 'POST' })
}

export async function getActiveReservationsApi(): Promise<ActiveReservationsDto> {
  return apiFetch<ActiveReservationsDto>('/user/reservations/active')
}
