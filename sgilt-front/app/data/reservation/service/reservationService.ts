import {
  getReservationMetaApi,
  cancelReservationApi,
  getActiveReservationsApi,
} from '../api/clientReservationApi'
import {
  getProReservationsApi,
  getProBoardCountsApi,
  getProReservationDetailApi,
  markContactedApi,
  confirmReservationApi,
  refuseReservationApi,
  cancelReservationByProApi,
} from '../api/proReservationApi'
import {
  mapReservationMeta,
  mapProReservationSummary,
  mapProReservationDetail,
  mapActiveReservations,
} from '../mapper/reservationMapper'
import type { ProBoardCounts } from '../domain/ProBoardCounts'
import type { ProReservationSummary } from '../domain/ProReservationSummary'
import type { ProReservationDetail } from '../domain/ProReservationDetail'
import type { ReservationMeta } from '../domain/ReservationMeta'
import type { ActiveReservations } from '../domain/ActiveReservation'

export async function fetchReservationMeta(reservationId: string): Promise<ReservationMeta> {
  return mapReservationMeta(await getReservationMetaApi(reservationId))
}

export async function cancelReservation(reservationId: string): Promise<void> {
  await cancelReservationApi(reservationId)
}

export async function fetchProReservations(): Promise<ProReservationSummary[]> {
  return (await getProReservationsApi()).map(mapProReservationSummary)
}

export async function fetchProBoardCounts(): Promise<ProBoardCounts> {
  return getProBoardCountsApi()
}

export async function fetchProReservationDetail(
  reservationId: string,
): Promise<ProReservationDetail> {
  return mapProReservationDetail(reservationId, await getProReservationDetailApi(reservationId))
}

export async function markDemandeContacted(reservationId: string): Promise<void> {
  await markContactedApi(reservationId)
}

export async function confirmReservation(reservationId: string): Promise<void> {
  await confirmReservationApi(reservationId)
}

export async function refuseReservation(reservationId: string, reason: string): Promise<void> {
  await refuseReservationApi(reservationId, reason)
}

export async function cancelReservationByPro(reservationId: string): Promise<void> {
  await cancelReservationByProApi(reservationId)
}

export async function fetchActiveReservations(): Promise<ActiveReservations> {
  return mapActiveReservations(await getActiveReservationsApi())
}
