import type { ReservationStatus } from './ReservationStatus'

export interface ProReservationSummary {
  id: string
  titre: string
  eventType?: string
  date: Date
  statut: ReservationStatus
  image?: string
  unreadNotesCount: number
}
