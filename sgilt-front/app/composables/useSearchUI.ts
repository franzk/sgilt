// app/composables/useSearchUI.ts
/**
 * Composable — état de la page de recherche de prestataires, porté par l'URL.
 * L'URL est la seule source de vérité : une recherche se partage, survit à un refresh et au retour
 * arrière. Aucun état local, donc chaque appelant (PrestataireSearch pour les filtres, useSearchFetch
 * pour la requête) lit la même chose.
 * - Paramètres de RECHERCHE `cat` / `subcats` : catégorie active et pastilles cochées.
 * - Paramètres d'AFFICHAGE `display-cats` / `display-subcats` : restriction à ce que regroupe une
 *   rubrique (recherche lancée depuis une rubrique, voir restrictedSearchPath) ; ils limitent les
 *   filtres visibles et ce qui est réellement cherché (searchedSubcats).
 */
import { ALL_CATEGORY_KEY } from '~/utils/constants'
import { useCategories } from '~/data/prestataire/useCategories'
import type { RubriqueContenu } from '~/data/template/domain/RubriqueContenu'

// Paramètres d'AFFICHAGE d'une recherche lancée depuis une rubrique : ce que la rubrique regroupe
// (catégories entières, sous-catégories isolées). Distincts des paramètres de RECHERCHE cat / subcats.
const DISPLAY_CATS_PARAM = 'display-cats'
const DISPLAY_SUBCATS_PARAM = 'display-subcats'

const csv = (raw: unknown) => (typeof raw === 'string' && raw ? raw.split(',').filter(Boolean) : [])

// Page de recherche restreinte au contenu d'une rubrique ; recherche normale si la rubrique ne
// regroupe rien (« autre »).
export function restrictedSearchPath(contenu: RubriqueContenu): string {
  const query = new URLSearchParams()
  if (contenu.categories.length > 0) query.set(DISPLAY_CATS_PARAM, contenu.categories.join(','))
  if (contenu.subcategories.length > 0) query.set(DISPLAY_SUBCATS_PARAM, contenu.subcategories.join(','))
  const search = query.toString()
  return search ? `/search?${search}` : '/search'
}

export function useSearchUi() {
  const route = useRoute()
  const router = useRouter()

  const defaultCategoryKey = ALL_CATEGORY_KEY

  // --- HELPERS POUR L'URL ---

  const updateQuery = (params: Record<string, string | undefined | null>) => {
    const patch: Record<string, string | undefined> = {}
    for (const [key, value] of Object.entries(params)) {
      patch[key] = value === null || value === '' ? undefined : (value ?? undefined)
    }
    router.replace({ query: { ...route.query, ...patch } })
  }

  // --- CATEGORIE ---
  // Stockée par clé dans l'URL (ex: ?cat=musique)

  const categoryKey = computed({
    get: () => (route.query.cat as string) || defaultCategoryKey,
    set: (val) => updateQuery({ cat: val, subcats: undefined }),
  })

  // --- SOUS-CATEGORIES ---
  // Stockées en CSV de clés dans l'URL (ex: ?subcats=dj,pop-rock)

  const currentSubcats = computed(() => csv(route.query.subcats))

  function toggleSubcat(subcatKey: string) {
    const current = [...currentSubcats.value]
    const index = current.indexOf(subcatKey)
    if (index > -1) {
      current.splice(index, 1)
    } else {
      current.push(subcatKey)
    }
    updateQuery({ subcats: current.length > 0 ? current.join(',') : undefined })
  }

  // --- AFFICHAGE RESTREINT ---
  // Catégories visibles : les catégories entières, puis les parentes des sous-catégories isolées.
  // Dans une catégorie entière tout est visible, sinon seulement les sous-catégories isolées.

  const { categories, subcategoriesOf } = useCategories()
  const displayCats = computed(() => csv(route.query[DISPLAY_CATS_PARAM]))
  const displaySubcats = computed(() => csv(route.query[DISPLAY_SUBCATS_PARAM]))
  const isRestricted = computed(() => displayCats.value.length > 0 || displaySubcats.value.length > 0)

  // Clés des catégories visibles, dans l'ordre du catalogue ; null : toutes.
  const visibleCategoryKeys = computed(() => {
    if (!isRestricted.value) return null
    return categories.value
      .filter(
        (category) =>
          displayCats.value.includes(category.key) ||
          category.subcategories.some((subcategory) => displaySubcats.value.includes(subcategory.key)),
      )
      .map((category) => category.key)
  })

  // Sous-catégories visibles d'une catégorie ; null : toutes.
  function visibleSubcatKeysOfCategory(key: string): string[] | null {
    if (!isRestricted.value || displayCats.value.includes(key)) return null
    const category = categories.value.find((candidate) => candidate.key === key)
    return (category?.subcategories ?? [])
      .map((subcategory) => subcategory.key)
      .filter((subcatKey) => displaySubcats.value.includes(subcatKey))
  }

  // Sous-catégories réellement cherchées : les pastilles actives, sinon, en mode restreint, ce qui
  // est affiché (« Tous » et une catégorie sans pastille ne débordent pas de la rubrique).
  const searchedSubcats = computed(() => {
    if (currentSubcats.value.length > 0 || !isRestricted.value) return currentSubcats.value
    if (categoryKey.value === ALL_CATEGORY_KEY) {
      return subcategoriesOf(displayCats.value, displaySubcats.value)
    }
    return visibleSubcatKeysOfCategory(categoryKey.value) ?? []
  })

  return {
    categoryKey,
    currentSubcats,
    searchedSubcats,
    visibleCategoryKeys,
    visibleSubcatKeysOfCategory,
    toggleSubcat,
    resetSubcats: () => updateQuery({ subcats: undefined }),
  }
}
