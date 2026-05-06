/**
 * Domaine — réponse d'une recherche de prestataires (résultats + compteurs)
 */
import type { PrestataireCardDetail } from './PrestataireCardDetail'

export interface PrestataireSearchResponse {
  results: PrestataireCardDetail[]
  countsByCategory: Record<string, number>
  subcatCounts: Record<string, number>
}
