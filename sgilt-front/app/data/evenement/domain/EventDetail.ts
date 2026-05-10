/**
 * Domaine — détail complet d'un événement (inclut réservations et journal)
 */
import type { Reservation } from '~/data/reservation/domain/Reservation'
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
  reservations: Reservation[]
  journal: JournalEntry[]
  mood: 'confirmee' | 'en_discussion' | 'nouvelle' | 'defaut'
  countdown: 'imminent' | 'proche' | 'serein' | 'past'
}
