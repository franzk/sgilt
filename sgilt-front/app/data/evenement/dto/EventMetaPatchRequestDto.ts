/**
 * DTO — corps de la requête PATCH /events/:id
 */
export interface EventMetaPatchRequestDto {
  title?: string
  lieu?: string
  sharedNote?: string
  eventType?: string
  ambiance?: string
  ville?: string
  nbInvites?: string
  description?: string
  momentCle?: string
}
