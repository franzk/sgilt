/**
 * Appels HTTP de l'espace prestataire sur ses réservations (/pro/reservations).
 */
import { apiFetch } from '~/composables/useApi'
import type { ProReservationDetailDto } from '../dto/ProReservationDetailDto'
import type { ProReservationSummaryDto } from '../dto/ProReservationSummaryDto'

export async function getProReservationsApi(): Promise<ProReservationSummaryDto[]> {
  return apiFetch<ProReservationSummaryDto[]>('/pro/reservations')
}

export async function getProReservationsToHandleApi(): Promise<ProReservationSummaryDto[]> {
  return apiFetch<ProReservationSummaryDto[]>('/pro/reservations/to-handle')
}

export async function getProConfirmedReservationsApi(): Promise<ProReservationSummaryDto[]> {
  return apiFetch<ProReservationSummaryDto[]>('/pro/reservations/confirmed')
}

export async function getProReservationDetailApi(
  reservationId: string,
): Promise<ProReservationDetailDto> {
  return apiFetch<ProReservationDetailDto>(`/pro/reservations/${reservationId}`)
}

export async function refuseReservationApi(reservationId: string, reason: string): Promise<void> {
  await apiFetch(`/pro/reservations/${reservationId}/refuse`, {
    method: 'POST',
    body: { reason },
  })
}

export async function cancelReservationByProApi(
  reservationId: string,
  reason?: string,
  isPersonal?: boolean,
): Promise<void> {
  await apiFetch(`/pro/reservations/${reservationId}/cancel`, {
    method: 'POST',
    body: { reason: reason ?? null, isPersonal: isPersonal ?? false },
  })
}
