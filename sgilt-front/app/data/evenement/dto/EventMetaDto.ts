/**
 * DTO — métadonnées d'un événement (partie meta de GET /events/:id, réponse de PATCH /events/:id
 * et de PUT /events/:id/horaires)
 */
import type { PublicPageStatus } from '~/data/ticketing/domain/OrganizerPublicPage'
export interface EventMetaDto {
  id: string
  title: string
  date?: string
  eventType?: string
  ambiance?: string
  ville?: string
  lieu?: string
  nbInvites?: string
  imagePath?: string | null
  sharedNote: string
  description?: string
  momentCle?: string
  lastUpdateDate?: string | null
  clientInfo: {
    firstName: string
    lastName: string
    phone?: string | null
    email: string
  }
  // État de la page publique, null si le back-office ne l'a pas activée.
  publicPageStatus?: PublicPageStatus | null
  // Horaires ('HH:mm'), null sans horaire.
  startTime?: string | null
  endTime?: string | null
}
