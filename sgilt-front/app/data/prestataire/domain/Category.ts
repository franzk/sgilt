/**
 * Domaine — référentiel des catégories et sous-catégories prestataire, servi par le back.
 * Les clés sont partagées front/back/DB ('musique', 'dj'…).
 */
export interface SubCategory {
  key: string
  name: string
  categoryKey: string
}

export interface Category {
  key: string
  name: string
  subcategories: SubCategory[]
}
