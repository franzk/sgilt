/**
 * DTO — métadonnées d'un événement (partie meta de GET /events/:id, réponse de PATCH /events/:id)
 */
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
  countdown: 'serein' | 'proche' | 'imminent' | 'past'
  lastUpdateDate?: string | null
  clientInfo: {
    firstName: string
    lastName: string
    phone?: string | null
    email: string
  }
}
