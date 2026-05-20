/**
 * Domaine — document joint à une réservation
 */
import type { ReservationFeed } from './ReservationFeed'

export interface ReservationDocument extends ReservationFeed {
  name: string
  fileType: 'pdf' | 'image' | 'other'
  url: string
}
