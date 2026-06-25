import type { EngagementKey } from '~/utils/constants'
import type { Testimonial } from '../domain/Testimonial'
import type { FaqItem } from '../domain/FaqItem'

/**
 * Payload PATCH /pro/me/fiche.
 * null = ne pas toucher le champ (jamais édité dans la session).
 * Valeur / "" / [] = écrire tel quel.
 */
export interface PrestataireUpdatePayload {
  baseline: string | null
  heroImage: string | null
  youtubeId: string | null
  shortDescription: string | null
  photos: string[] | null
  badges: EngagementKey[] | null
  offerings: string[] | null
  identity: { quote: string | null; bio: string | null } | null
  budget: string | null
  testimonials: Testimonial[] | null
  logistics: string[] | null
  technical: string[] | null
  faq: FaqItem[] | null
}
