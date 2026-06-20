/**
 * DTO — contrat API pour la fiche complète d'un prestataire
 */
import type { PrestataireCardDto } from './PrestataireCardDto'
import type { EngagementKey } from '~/utils/constants'

export interface PrestataireDetailDto extends PrestataireCardDto {
  baseline: string
  youtubeId?: string
  subcatKeys: string[]
  photos: string[]
  badges: EngagementKey[]
  offerings: string[]
  identity?: { quote: string; bio: string }
  budget: string | null
  testimonials?: { author: string; text: string; eventType?: string }[]
  logistics?: string[]
  technical?: string[]
  faq?: { question: string; answer: string }[]
}
