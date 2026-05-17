/**
 * Domaine — résumé d'une réservation pour l'affichage en liste
 */
import type { ReservationStatus } from './ReservationStatus'

export interface ReservationSummary {
  id: string
  prestataireId: string
  prestataireName: string
  prestatairePhoto?: string
  category: string
  status: ReservationStatus
  unreadNotesCount: number
}
