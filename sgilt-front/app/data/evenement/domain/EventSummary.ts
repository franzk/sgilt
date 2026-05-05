/**
 * Domaine — résumé d'un événement pour les vues liste
 */
export interface EventSummary {
  id: string
  title: string
  date?: Date
  ville?: string
  coverImage?: string | null
  eventType?: string
  confirmedCount: number
  inDiscussionCount: number
}
