/**
 * DTO — contrat API pour la réponse de recherche de prestataires
 */
import type { PrestataireCardDto } from './PrestataireCardDto'

export interface PrestataireSearchResponseDto {
  results: PrestataireCardDto[]
  countsByCategory: Record<string, number>
  subcatCounts: Record<string, number>
}
