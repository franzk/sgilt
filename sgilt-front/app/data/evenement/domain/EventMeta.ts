/**
 * Domaine — métadonnées d'un événement en base (données, horaires, compte à rebours, dernière
 * modification, état de la page publique)
 */
import type { PublicPageStatus } from '~/data/ticketing/domain/OrganizerPublicPage'

export interface EventMeta {
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
  lastUpdateDate?: Date | null
  // État de la page publique, absent si le back-office ne l'a pas activée.
  publicPageStatus?: PublicPageStatus
  // Horaires ('HH:mm'), absents sans horaire ; une fin avant le début tombe le lendemain.
  startTime?: string
  endTime?: string
}
