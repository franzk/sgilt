/**
 * Composable — référentiel des catégories et sous-catégories prestataire.
 * Contrairement aux autres composables du dossier (ref au niveau module), l'état vit dans un
 * useState : le référentiel est chargé côté serveur (plugin categories) pour le rendu SSR, puis
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
      console.error('[categories] Échec du chargement du référentiel :', e)
    }
  }

  // Libellé d'une catégorie ; la clé brute tant que le référentiel n'est pas chargé.
  function categoryName(key: string | undefined): string {
    if (!key) return ''
    return categories.value.find((category) => category.key === key)?.name ?? key
  }

  // Libellé d'une sous-catégorie ; la clé brute tant que le référentiel n'est pas chargé.
  function subcategoryName(key: string | undefined): string {
    if (!key) return ''
    return (
      categories.value
        .flatMap((category) => category.subcategories)
        .find((subcategory) => subcategory.key === key)?.name ?? key
    )
  }

  return { categories, load, categoryName, subcategoryName }
}
