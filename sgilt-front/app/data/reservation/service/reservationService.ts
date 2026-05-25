import { getReservationMetaApi, cancelReservationApi, getActiveReservationsApi } from '../api/reservationApi'
import type { ActiveReservations, ActiveReservationItem } from '../domain/ActiveReservation'
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
