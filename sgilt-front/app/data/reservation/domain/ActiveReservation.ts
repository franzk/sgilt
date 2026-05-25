import type { ReservationStatus } from './ReservationStatus'

export interface ActiveReservationItem {
  reservationId: string
  status: ReservationStatus
  evenementId: string
  evenementTitle: string
  prestataireSlug: string
  prestataireName: string
  prestataireAvatar: string | null
}

export interface ActiveReservations {
  items: ActiveReservationItem[]
  hasConfirmed: boolean
}
