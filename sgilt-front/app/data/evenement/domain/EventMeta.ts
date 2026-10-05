/**
 * Domaine — métadonnées d'un événement en base (données, compte à rebours, dernière modification)
 */

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
  countdown: 'imminent' | 'proche' | 'serein' | 'past'
  lastUpdateDate?: Date | null
}
