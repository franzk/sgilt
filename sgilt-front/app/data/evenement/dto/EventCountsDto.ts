/**
 * DTO — contrat de réponse de l'API GET /events/:id/counts
 */
export interface EventCountsDto {
  mood: 'confirmee' | 'en_discussion' | 'nouvelle' | 'defaut'
  confirmedCount: number
  inDiscussionCount: number
  nouvelleCount: number
  refuseeCount: number
  annuleeCount: number
  realiseeCount: number
}
