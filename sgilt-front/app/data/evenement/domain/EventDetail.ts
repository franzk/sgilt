/**
 * Domaine — détail complet d'un événement (inclut réservations et journal)
 */
import type { ReservationSummary } from '~/data/reservation/domain/ReservationSummary'
import type { JournalEntry } from './JournalEntry'

export interface EventDetail {
  id: string
  title: string
  date?: Date
  eventType?: string
  ambiance?: string
  ville?: string
  lieu?: string
  nbInvites?: string
  coverImage?: string | null
  sharedNote: string
  sharedNoteUpdatedAt?: Date
  description?: string
  momentCle?: string
  reservations: ReservationSummary[]
  journal: JournalEntry[]
  mood: 'confirmee' | 'en_discussion' | 'nouvelle' | 'defaut'
  countdown: 'imminent' | 'proche' | 'serein' | 'past'
  lastUpdateDate?: Date | null
}
