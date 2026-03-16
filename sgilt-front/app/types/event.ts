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
  prestataireId: string
  prestataireName: string
  prestatairePhoto?: string
  category: string
  status: ReservationStatus
  unreadNotesCount: number
}

export interface EventDetail {
  id: string
  title: string
  date?: string // ISO date (YYYY-MM-DD)
  eventType?: string // valeur de EVENT_TYPE_OPTIONS
  ambiance?: string // valeur de AMBIANCE_OPTIONS
  ville?: string
  nbInvites?: string
  sharedNote: string
  sharedNoteUpdatedAt?: string // ISO datetime
  reservations: Reservation[]
  journal: JournalEntry[]
}

export interface JournalModification {
  champ: string
  avant: string | null // null pour la création
  apres: string
}

export interface JournalEntry {
  id: string
  date: Date
  modifications: JournalModification[]
  isCreation: boolean
}

export type EventPatch = Partial<
  Pick<EventDetail, 'title' | 'eventType' | 'ambiance' | 'ville' | 'nbInvites' | 'sharedNote'>
>

export interface NoteAuthor {
  id: string
  name: string
  photo?: string
  role: 'client' | 'prestataire'
}

export interface ReservationNote {
  id: string
  author: NoteAuthor
  content: string
  createdAt: string // ISO datetime
}

export interface ReservationDetail extends Reservation {
  notes: ReservationNote[]
}
