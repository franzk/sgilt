/**
 * Domaine — profil complet d'un prestataire (fiche détail)
 */
import type { EngagementBadge } from '~/utils/constants'
import type { PrestataireCardDetail } from './PrestataireCardDetail'
import type { Testimonial } from './Testimonial'
import type { FaqItem } from './FaqItem'

export interface PrestataireDetail extends PrestataireCardDetail {
  baseline: string
  categoryKey: string
  category: string
  subcats: string[]
  heroImage: string
  youtubeId?: string
  photos: string[]
  badges: EngagementBadge[]
  offerings: string[]
  identity?: { quote: string; bio: string }
  budget?: string
  unavailableDates?: string[]
  testimonials?: Testimonial[]
  logistics?: string[]
  technical?: string[]
  faq?: FaqItem[]
}
