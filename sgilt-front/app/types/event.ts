export type ReservationStatus =
  | 'brouillon'
  | 'envoyee'
  | 'recontactee'
  | 'confirmee'
  | 'annulee'
  | 'cloturee'
  | 'terminee'

export interface Reservation {
  id: string
  prestataireName: string
  prestatairePhoto?: string
  category: string
  status: ReservationStatus
  hasNotes: boolean
}

export interface EventDetail {
  id: string
  title: string
  date?: string         // ISO date (YYYY-MM-DD)
  eventType?: string    // valeur de EVENT_TYPE_OPTIONS
  ambiance?: string     // valeur de AMBIANCE_OPTIONS
  ville?: string
  nbInvites?: string
  sharedNote: string
  sharedNoteUpdatedAt?: string // ISO datetime
  reservations: Reservation[]
}
