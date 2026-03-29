export type ReservationStatus =
  | 'nouvelle'
  | 'en_discussion'
  | 'confirmee'
  | 'refusee'
  | 'annulee'
  | 'realisee'

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
  lieu?: string // lieu précis (salle, adresse)
  nbInvites?: string
  coverImage?: string | null
  sharedNote: string
  sharedNoteUpdatedAt?: string // ISO datetime
  reservations: Reservation[]
  journal: JournalEntry[]
  phrase: string
  phraseSubtitle: string
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
  Pick<EventDetail, 'title' | 'coverImage' | 'eventType' | 'ambiance' | 'ville' | 'lieu' | 'nbInvites' | 'sharedNote'>
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
  isPersonal?: boolean       // note privée pro uniquement
  isMessageInitial?: boolean // premier message du tunnel demande
}

export interface ReservationDocument {
  id: string
  name: string
  fileType: 'pdf' | 'image' | 'other'
  url: string
  uploadedBy: NoteAuthor
  uploadedAt: string // ISO datetime
}

// ── Feed items (ReservationFeed) ───────────────────────────────────────────────

export type FeedNote = ReservationNote & { _kind: 'note' }
export type FeedDocument = ReservationDocument & { _kind: 'document' }
export type FeedItem = FeedNote | FeedDocument

export interface ReservationDetail extends Reservation {
  notes: ReservationNote[]
  documents: ReservationDocument[]
  urgencyLevel: number
  ligneContextuelle: string
}

// ── Types pro ─────────────────────────────────────────────────────────────────

export interface ClientContactInfo {
  firstName: string
  lastName?: string
  phone: string
  email: string
}

export interface ProDemandeDetail extends ReservationDetail {
  event: EventDetail
  progressType: 'deadline' | 'duration' | 'temporal' | null
  progressValue: number | null
  phraseInfoState: string | null
  clientInfo: ClientContactInfo
}

export interface ProDemandeSummary {
  id: string
  titre: string
  date: string         // date événement formatée (e.g. "14 septembre 2026")
  dateIso: string
  dateReception: string // date de réception de la demande formatée
  statut: ReservationStatus
  ligneContextuelle: string
  urgencyLevel: number
  coverImage?: string
  progressType: 'deadline' | 'duration' | 'temporal' | null
  progressValue: number | null
  phraseInfoState: string | null
  unreadNotesCount: number
}
