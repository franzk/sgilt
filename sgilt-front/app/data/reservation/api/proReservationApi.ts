import { apiFetch } from '~/composables/useApi'
import type { ProBoardCountsDto } from '../dto/ProBoardCountsDto'
import type { ProReservationDetailDto } from '../dto/ProReservationDetailDto'
import type { ProReservationSummaryDto } from '../dto/ProReservationSummaryDto'

export async function getProReservationsApi(): Promise<ProReservationSummaryDto[]> {
  return apiFetch<ProReservationSummaryDto[]>('/pro/reservations')
}

export async function getProBoardCountsApi(): Promise<ProBoardCountsDto> {
  return apiFetch<ProBoardCountsDto>('/pro/reservations/counts')
}

export async function getProReservationDetailApi(
  reservationId: string,
): Promise<ProReservationDetailDto> {
  return apiFetch<ProReservationDetailDto>(`/pro/reservations/${reservationId}`)
}

export async function markContactedApi(reservationId: string): Promise<void> {
  await apiFetch(`/pro/reservations/${reservationId}/mark-contacted`, { method: 'POST' })
}

export async function confirmReservationApi(reservationId: string): Promise<void> {
  await apiFetch(`/pro/reservations/${reservationId}/confirm`, { method: 'POST' })
}

export async function refuseReservationApi(reservationId: string, reason: string): Promise<void> {
  await apiFetch(`/pro/reservations/${reservationId}/refuse`, {
    method: 'POST',
    body: { reason },
  })
}

export async function cancelReservationByProApi(reservationId: string): Promise<void> {
  await apiFetch(`/pro/reservations/${reservationId}/cancel`, { method: 'POST' })
}
