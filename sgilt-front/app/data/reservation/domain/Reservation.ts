/**
 * Domaine — résumé d'une réservation (lié à un prestataire)
 */
import type { ReservationStatus } from './ReservationStatus'

export interface Reservation {
  id: string
  prestataireId: string
  prestataireName: string
  prestatairePhoto?: string
  category: string
  status: ReservationStatus
  unreadNotesCount: number
}
