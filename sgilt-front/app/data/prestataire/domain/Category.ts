/**
 * Domaine — catégories et sous-catégories de prestataire, servies par le back.
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
