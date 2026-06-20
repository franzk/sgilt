/**
 * Domaine — profil complet d'un prestataire (fiche détail)
 */
import type { EngagementKey } from '~/utils/constants'
import type { PrestataireCardDetail } from './PrestataireCardDetail'
import type { Testimonial } from './Testimonial'
import type { FaqItem } from './FaqItem'

export interface PrestataireDetail extends PrestataireCardDetail {
  baseline: string
  categoryKey: string
  category: string
  subcats: string[]
  heroImage: string
  youtubeId: string | null
  photos: string[]
  badges: EngagementKey[]
  offerings: string[]
  identity: { quote: string | null; bio: string | null }
  budget: string | null
  unavailableDates: string[]
  testimonials: Testimonial[]
  logistics: string[]
  technical: string[]
  faq: FaqItem[]
}
