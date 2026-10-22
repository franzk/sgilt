/**
 * Domaine — ce que regroupe une rubrique : catégories entières et sous-catégories isolées (de quoi
 * limiter une recherche de prestataires à la rubrique).
 */
export interface RubriqueContenu {
  categories: string[]
  subcategories: string[]
}
