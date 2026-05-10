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
  countdown: 'serein' | 'proche' | 'imminent' | 'past'
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
}
