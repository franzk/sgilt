import { getReservationMetaApi, cancelReservationApi } from '../api/reservationApi'
import type { ReservationMeta } from '../domain/ReservationMeta'

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
