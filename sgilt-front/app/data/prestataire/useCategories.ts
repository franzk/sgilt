/**
 * Composable — catégories et sous-catégories de prestataire.
 * Contrairement aux autres composables du dossier (ref au niveau module), l'état vit dans un
 * useState : les catégories sont chargées côté serveur (plugin categories) pour le rendu SSR, puis
 * transmis au client dans le payload. Un ref de module serait partagé entre toutes les requêtes
 * du serveur et resterait vide à l'hydratation.
 */
import { fetchCategories } from './service/prestataireService'
import type { Category } from './domain/Category'

export function useCategories() {
  const categories = useState<Category[]>('categories', () => [])

  async function load() {
    if (categories.value.length > 0) return
    try {
      categories.value = await fetchCategories()
    } catch (e) {
      console.error('[categories] Échec du chargement des catégories :', e)
    }
  }

  // Libellé d'une catégorie ; la clé brute tant que les catégories ne sont pas chargées.
  function categoryName(key: string | undefined): string {
    if (!key) return ''
    return categories.value.find((category) => category.key === key)?.name ?? key
  }

  // Libellé d'une sous-catégorie ; la clé brute tant que les catégories ne sont pas chargées.
  function subcategoryName(key: string | undefined): string {
    if (!key) return ''
    return (
      categories.value
        .flatMap((category) => category.subcategories)
        .find((subcategory) => subcategory.key === key)?.name ?? key
    )
  }

  // Sous-catégories couvertes par des catégories entières et des sous-catégories isolées (le
  // contenu d'une rubrique) : celles des catégories, puis les isolées.
  function subcategoriesOf(categoryKeys: string[], subcategoryKeys: string[]): string[] {
    const ofCategories = categories.value
      .filter((category) => categoryKeys.includes(category.key))
      .flatMap((category) => category.subcategories.map((subcategory) => subcategory.key))
    return [...new Set([...ofCategories, ...subcategoryKeys])]
  }

  return { categories, load, categoryName, subcategoryName, subcategoriesOf }
}
