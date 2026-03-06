// app/types/prestataire.ts

import type { EngagementBadge } from '~/utils/constants'

// ─── Sous-types ───────────────────────────────────────────────────────────────

export interface Testimonial {
  author: string
  text: string
  eventType?: string // ex: "Mariage", "Anniversaire"
}

export interface FaqItem {
  question: string
  answer: string
}

// ─── Card (liste de résultats) ────────────────────────────────────────────────

export interface PrestataireCardDetail {
  id: string
  name: string
  shortDescription: string
  image: string
  slug: string
  categoryPicto?: Component
  categoryName?: string
}

// ─── Fiche complète ───────────────────────────────────────────────────────────

export interface PrestataireDetail extends PrestataireCardDetail {
  // Identité
  baseline: string // 1 phrase d'accroche
  categoryId: string
  category: string // "Musique", "Restauration"...
  subcats: string[] // IDs des sous-catégories

  // Hero media
  heroImage: string // image principale forte
  photos: string[] // 2-3 images supplémentaires
  youtubeId?: string // ID YouTube (optionnel)

  // Contenu
  badges: EngagementBadge[] // 2-4 max
  offerings: string[] // "Ce que nous proposons" — liste libre
  identity?: { quote: string; bio: string } // Touche identitaire (optionnel)
  budget?: string // Texte libre du prestataire (optionnel)

  // Disponibilités
  unavailableDates: string[] // ISO dates (ex: "2026-06-14")

  // Témoignages
  testimonials?: Testimonial[] // 0-2 max

  // Infos pratiques (flux mobile : logistique → technique → FAQ)
  logistics?: string[] // liste libre
  technical?: string[] // liste libre
  faq?: FaqItem[]
}

// ─── Réponse de recherche ─────────────────────────────────────────────────────

export interface PrestataireSearchResponse {
  results: PrestataireCardDetail[]
  countsByCategory: Record<string, number>
  subcatCounts: Record<string, number>
}
