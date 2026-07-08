/**
 * Domaine — profil complet d'un prestataire (fiche détail)
 */
import type { EngagementKey } from '~/utils/constants'
import type { Testimonial } from './Testimonial'
import type { FaqItem } from './FaqItem'
import type { Media } from './Media'
import type { PrestataireStatus } from './PrestataireStatus'

export interface PrestataireDetail {
  id: string
  name: string
  shortDescription: string
  slug: string
  baseline: string
  categoryKey: string
  category: string
  subcats: string[]
  avatar: string | null
  medias: Media[]
  badges: EngagementKey[]
  offerings: string[]
  identity: { quote: string | null; bio: string | null }
  budget: string | null
  unavailableDates: string[]
  testimonials: Testimonial[]
  logistics: string[]
  technical: string[]
  faq: FaqItem[]
  status: PrestataireStatus
}
