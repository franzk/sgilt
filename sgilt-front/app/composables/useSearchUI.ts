// app/composables/useSearchUI.ts
import { APP_CATEGORIES } from '~/utils/constants'

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
    router.replace({
      query: {
        ...route.query,
        ...params,
      },
    })
  }

  // --- DATE ---

  const date = computed({
    get: () => (route.query.date as string) || toISODate(new Date()),
    set: (val) => updateQuery({ date: val }),
  })

  const dateModel = computed({
    get: () => {
      const d = new Date(date.value)
      return isNaN(d.getTime()) ? new Date() : d
    },
    set: (value: Date) => {
      date.value = toISODate(value)
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
    categoryId,
    showOnboarding,
    currentSubcats,
    toggleSubcat,
    resetSubcats: () => updateQuery({ subcats: undefined }),
    toISODate,
  }
}

export function toISODate(d: Date): string {
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd}`
}
