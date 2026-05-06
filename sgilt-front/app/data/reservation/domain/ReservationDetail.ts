/**
 * Domaine — détail complet d'une réservation (notes, documents, contexte)
 */
import type { Reservation } from './Reservation'
import type { ReservationNote } from './ReservationNote'
import type { ReservationDocument } from './ReservationDocument'

export interface ReservationDetail extends Reservation {
  notes: ReservationNote[]
  documents: ReservationDocument[]
  urgencyLevel: number
  ligneContextuelle: string
}
