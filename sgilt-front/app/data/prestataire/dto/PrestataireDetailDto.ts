/**
 * DTO — contrat API pour la fiche complète d'un prestataire
 */
import type { EngagementKey } from '~/utils/constants'
import type { Media } from '../domain/Media'

export interface PrestataireDetailDto {
  id: string
  name: string
  shortDescription: string
  slug: string
  categoryKey: string
  baseline: string
  avatar: string | null
  subcatKeys: string[]
  medias: Media[]
  badges: EngagementKey[]
  offerings: string[]
  identity?: { quote: string; bio: string }
  budget: string | null
  testimonials?: { author: string; text: string; eventType?: string }[]
  logistics?: string[]
  technical?: string[]
  faq?: { question: string; answer: string }[]
}
