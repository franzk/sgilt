/**
 * DTO — contrat API pour un prestataire en résultat de recherche
 */
export interface PrestataireCardDto {
  id: string
  name: string
  shortDescription: string
  heroImage: string
  slug: string
  categoryKey: string
}
