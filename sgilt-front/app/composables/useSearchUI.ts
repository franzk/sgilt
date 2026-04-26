// app/composables/useSearchUI.ts
import { APP_CATEGORIES } from '~/utils/constants'
import { toISODate } from '~/utils/dateUtils'

export function useSearchUi() {
  const route = useRoute()
  const router = useRouter()

  const defaultCategoryKey = APP_CATEGORIES[0]?.key ?? 'all'

  // --- HELPERS POUR L'URL ---

  const updateQuery = (params: Record<string, string | undefined | null>) => {
    const patch: Record<string, string | undefined> = {}
    for (const [key, value] of Object.entries(params)) {
      patch[key] = value === null || value === '' ? undefined : (value ?? undefined)
    }
    router.replace({ query: { ...route.query, ...patch } })
  }

  // --- DATE ---

  const date = computed({
    get: () => (route.query.date as string) || '',
    set: (val) => updateQuery({ date: val }),
  })

  const stateDate = useState<Date | undefined>('search:date', () => undefined)

  watch(
    date,
    (val) => { if (val) stateDate.value = new Date(val) },
    { immediate: true },
  )

  const dateModel = computed({
    get: () => (date.value ? new Date(date.value) : stateDate.value),
    set: (value: Date | undefined) => {
      date.value = value ? toISODate(value) : ''
      stateDate.value = value
    },
  })

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

  // --- ONBOARDING ---

  const showOnboarding = useState<boolean>('search:showOnboarding', () => false)

  return {
    date,
    dateModel,
    stateDate,
    categoryKey,
    showOnboarding,
    currentSubcats,
    toggleSubcat,
    resetSubcats: () => updateQuery({ subcats: undefined }),
  }
}
