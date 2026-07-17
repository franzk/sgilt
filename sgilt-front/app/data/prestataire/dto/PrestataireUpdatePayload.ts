import type { EngagementKey } from '~/utils/constants'
import type { Testimonial } from '../domain/Testimonial'
import type { FaqItem } from '../domain/FaqItem'
import type { DetailItem } from '../domain/DetailItem'

/**
 * Payload PATCH /pro/me/fiche.
 * null = ne pas toucher le champ (jamais édité dans la session).
 * Valeur / "" / [] = écrire tel quel.
 */
export interface PrestataireUpdatePayload {
  baseline: string | null
  shortDescription: string | null
  badges: EngagementKey[] | null
  offerings: string[] | null
  identity: { quote: string | null; bio: string | null } | null
  budget: string | null
  testimonials: Testimonial[] | null
  details: DetailItem[] | null
  faq: FaqItem[] | null
}
