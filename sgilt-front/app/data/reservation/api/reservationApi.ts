import { apiFetch } from '~/composables/useApi'
import type { ActiveReservationsDto } from '../dto/ActiveReservationsDto'
import type { ProBoardCountsDto } from '../dto/ProBoardCountsDto'
import type { ProReservationSummaryDto } from '../dto/ProReservationSummaryDto'
import type { ReservationMetaDto } from '../dto/ReservationMetaDto'
import type { EventReservationSummaryDto } from '~/data/evenement/dto/EventReservationSummaryDto'

export async function getReservationsByEventApi(eventId: string): Promise<EventReservationSummaryDto[]> {
  return apiFetch<EventReservationSummaryDto[]>(`/reservations`, { params: { eventId } })
}

export async function getReservationMetaApi(reservationId: string): Promise<ReservationMetaDto> {
  return apiFetch<ReservationMetaDto>(`/reservations/${reservationId}`)
}

export async function cancelReservationApi(reservationId: string): Promise<void> {
  await apiFetch(`/reservations/${reservationId}/cancel`, { method: 'POST' })
}

export async function getActiveReservationsApi(): Promise<ActiveReservationsDto> {
  return apiFetch<ActiveReservationsDto>('/reservations/active')
}

export async function getProReservationsApi(): Promise<ProReservationSummaryDto[]> {
  return apiFetch<ProReservationSummaryDto[]>('/reservations/pro')
}

export async function getProBoardCountsApi(): Promise<ProBoardCountsDto> {
  return apiFetch<ProBoardCountsDto>('/reservations/pro/counts')
}
