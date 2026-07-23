/**
 * DTO — contrat API pour la génération IA de fiche prestataire
 */
import type { Testimonial } from '~/data/prestataire/domain/Testimonial'
import type { DetailItem } from '~/data/prestataire/domain/DetailItem'
import type { FaqItem } from '~/data/prestataire/domain/FaqItem'

export interface FicheIaGenerationContentDto {
  shortDescription: string
  baseline: string
  offerings: string[]
  identity: { quote: string; bio: string }
  testimonials: Testimonial[]
  details: DetailItem[]
  faq: FaqItem[]
  budget: string
}

export interface FicheIaGenerationResultDto {
  result: FicheIaGenerationContentDto | null
  triesLeft: number
  lastGenerationDateTime: string | null
}
