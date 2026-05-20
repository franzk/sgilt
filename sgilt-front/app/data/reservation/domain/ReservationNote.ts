/**
 * Domaine — note échangée dans le fil d'une réservation
 */
import type { ReservationFeed } from './ReservationFeed'

export interface ReservationNote extends ReservationFeed {
  content: string
  isPersonal?: boolean
  isMessageInitial?: boolean
}
