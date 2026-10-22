import type { EventMetaDto } from './EventMetaDto'
import type { EventRubriqueDto } from './EventRubriqueDto'

/**
 * DTO — contrat de réponse de l'API GET /events/:id : tout l'événement
 */
export interface EventDto {
  meta: EventMetaDto
  rubriques: EventRubriqueDto[]
}
