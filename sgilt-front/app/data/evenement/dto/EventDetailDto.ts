/**
 * DTO — contrat de réponse de l'API GET /events/:id
 */
export interface EventDetailDto {
  id: string
  title: string
  date?: string
  eventType?: string
  ambiance?: string
  ville?: string
  lieu?: string
  nbInvites?: string
  coverUrl?: string | null
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

/**
 * DTO — corps de la requête PATCH /events/:id
 */
export interface EventPatchRequestDto {
  lieu?: string
  sharedNote?: string
  eventType?: string
  ambiance?: string
  ville?: string
  nbInvites?: string
  description?: string
  momentCle?: string
}
