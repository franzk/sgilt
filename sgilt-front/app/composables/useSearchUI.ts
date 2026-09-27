// app/composables/useSearchUI.ts
import { ALL_CATEGORY_KEY } from '~/utils/constants'

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

  const currentSubcats = computed(() => {
    const raw = route.query.subcats as string
    return raw ? raw.split(',').filter(Boolean) : []
  })

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

  return {
    categoryKey,
    currentSubcats,
    toggleSubcat,
    resetSubcats: () => updateQuery({ subcats: undefined }),
  }
}
