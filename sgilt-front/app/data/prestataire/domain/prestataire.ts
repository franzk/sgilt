import type { EngagementBadge } from '~/utils/constants'

export interface Testimonial {
  author: string
  text: string
  eventType?: string
}

export interface FaqItem {
  question: string
  answer: string
}

export interface PrestataireCardDetail {
  id: string
  name: string
  shortDescription: string
  image: string
  slug: string
  categoryKey?: string
  categoryName?: string
}

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

export interface PrestataireSearchResponse {
  results: PrestataireCardDetail[]
  countsByCategory: Record<string, number>
  subcatCounts: Record<string, number>
}
