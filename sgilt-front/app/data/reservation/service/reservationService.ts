import { getReservationMetaApi, cancelReservationApi, getActiveReservationsApi, getProReservationsApi, getProBoardCountsApi } from '../api/reservationApi'
import type { ActiveReservations, ActiveReservationItem } from '../domain/ActiveReservation'
import type { ProBoardCounts } from '../domain/ProBoardCounts'
import type { ProReservationSummary } from '../domain/ProReservationSummary'
import type { ReservationMeta } from '../domain/ReservationMeta'
import type { ReservationStatus } from '../domain/ReservationStatus'

export async function fetchReservationMeta(reservationId: string): Promise<ReservationMeta> {
  const dto = await getReservationMetaApi(reservationId)
  return {
    id: dto.id,
    prestataireId: dto.prestataireId,
    prestataireName: dto.prestataireName,
    prestatairePhoto: dto.prestatairePhoto ?? undefined,
    category: dto.category,
    status: dto.status as ReservationMeta['status'],
    unreadNotesCount: dto.unreadNotesCount,
  }
}

export async function cancelReservation(reservationId: string): Promise<void> {
  await cancelReservationApi(reservationId)
}

export async function fetchProReservations(): Promise<ProReservationSummary[]> {
  const dtos = await getProReservationsApi()
  return dtos.map((dto) => ({
    id: dto.id,
    titre: dto.evenementTitre,
    date: new Date(dto.datePrestation),
    statut: dto.statut as ReservationStatus,
    image: dto.image ?? undefined,
    unreadNotesCount: dto.unreadNotesCount,
  }))
}

export async function fetchProBoardCounts(): Promise<ProBoardCounts> {
  return getProBoardCountsApi()
}

export async function fetchActiveReservations(): Promise<ActiveReservations> {
  const dto = await getActiveReservationsApi()
  const items: ActiveReservationItem[] = dto.items.map((item) => ({
    reservationId: item.reservationId,
    status: item.status as ReservationStatus,
    evenementId: item.evenementId,
    evenementTitle: item.evenementTitle,
    prestataireSlug: item.prestataireSlug,
    prestataireName: item.prestataireName,
    prestataireAvatar: item.prestataireAvatar,
  }))
  return { items, hasConfirmed: dto.hasConfirmed }
}
