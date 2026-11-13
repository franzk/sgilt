/**
 * Domaine — métadonnées d'un événement en base (données, horaires, compte à rebours, dernière
 * modification, état de la page publique, billetterie activée)
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
  // Billetterie activée par le back-office (jamais sans page publique) ; absent hors de l'espace
  // organisateur (fiche réservation côté pro).
  billetterieEnabled?: boolean
  // Horaires ('HH:mm'), absents sans horaire ; une fin avant le début tombe le lendemain.
  startTime?: string
  endTime?: string
}
