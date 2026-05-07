/**
 * Domaine — compteurs et humeur d'un événement (depuis /events/:id/counts)
 */
export interface EventCounts {
  mood: 'confirmee' | 'en_discussion' | 'nouvelle' | 'defaut'
  confirmedCount: number
  inDiscussionCount: number
  nouvelleCount: number
  refuseeCount: number
  annuleeCount: number
  realiseeCount: number
}
