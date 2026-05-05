/**
 * Domaine — résumé d'un prestataire pour les cartes de liste et de recherche
 */
export interface PrestataireCardDetail {
  id: string
  name: string
  shortDescription: string
  image: string
  slug: string
  categoryKey?: string
  categoryName?: string
}
