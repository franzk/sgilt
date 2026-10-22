/**
 * Domaine — catégories et sous-catégories prestataire, telles qu'administrées au back-office.
 */
export interface SousCategorieAdmin {
  key: string
  name: string
  categoryKey: string
  prestataireCount: number
}

export interface CategorieAdmin {
  key: string
  name: string
  subcategories: SousCategorieAdmin[]
}

export type MoveDirection = 'UP' | 'DOWN'
