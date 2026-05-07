/**
 * DTO — contrat de réponse de l'API GET /events (liste)
 */
export interface EvenementSummaryDto {
  id: string
  name: string
  date?: string
  ville?: string
  coverUrl?: string | null
  eventType?: string
  confirmedCount: number
  inDiscussionCount: number
}
