// app/composables/useSearchUI.ts
import { APP_CATEGORIES } from '~/utils/constants'
import { toISODate } from '~/utils/dateUtils'

export function useSearchUi() {
  const route = useRoute()
  const router = useRouter()

  const defaultCatId = (() => {
    const id = APP_CATEGORIES[0]?.id
    if (!id) console.warn('[useSearchUi] APP_CATEGORIES est vide ou mal configuré')
    return id || ''
  })()

  // --- HELPERS POUR L'URL ---

  const updateQuery = (params: Record<string, string | undefined | null>) => {
    // Les clés à supprimer sont mises à undefined pour que Vue Router les retire de l'URL
    const patch: Record<string, string | undefined> = {}
    for (const [key, value] of Object.entries(params)) {
      patch[key] = value === null || value === '' ? undefined : (value ?? undefined)
    }

    router.replace({
      query: {
        ...route.query,
        ...patch,
      },
    })
  }

  // --- DATE ---

  const date = computed({
    get: () => (route.query.date as string) || '',
    set: (val) => updateQuery({ date: val }),
  })

  const stateDate = useState<Date | undefined>('search:date', () => undefined)

  // Quand l'URL contient une date (ex: arrivée depuis index), on la propage dans stateDate
  // pour que [slug] puisse la lire sans ?date= dans son URL
  watch(
    date,
    (val) => {
      if (val) stateDate.value = new Date(val)
    },
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

  const categoryId = computed({
    get: () => (route.query.cat as string) || defaultCatId,
    set: (val) => {
      // On réinitialise les sous-catégories si on change de catégorie parente
      updateQuery({ cat: val, subcats: undefined })
    },
  })

  // --- SOUS-CATEGORIES ---
  // Stockées en CSV dans l'URL (ex: ?subcats=12,45,67)

  const currentSubcats = computed(() => {
    const raw = route.query.subcats as string
    return raw ? raw.split(',') : []
  })

  function toggleSubcat(subcatId: string) {
    const current = [...currentSubcats.value]
    const index = current.indexOf(subcatId)
    if (index > -1) {
      current.splice(index, 1)
    } else {
      current.push(subcatId)
    }
    updateQuery({
      subcats: current.length > 0 ? current.join(',') : undefined,
    })
  }

  // --- ONBOARDING (useState car c'est purement UI/Session) ---

  const showOnboarding = useState<boolean>('search:showOnboarding', () => false)

  return {
    date,
    dateModel,
    stateDate,
    categoryId,
    showOnboarding,
    currentSubcats,
    toggleSubcat,
    resetSubcats: () => updateQuery({ subcats: undefined }),
  }
}
