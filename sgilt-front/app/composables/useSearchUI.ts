// app/composables/useSearchUI.ts
import { APP_CATEGORIES } from '~/utils/constants'

export function useSearchUi() {
  const route = useRoute()
  const router = useRouter()

  const defaultCatId = APP_CATEGORIES[0]?.id || '1'

  // --- HELPERS POUR L'URL ---
  // Met à jour une clé dans l'URL sans recharger la page
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
    get: () => new Date(date.value),
    set: (value: Date) => {
      date.value = toISODate(value)
    },
  })

  // --- CATEGORIE ---
  const categoryId = computed({
    get: () => (route.query.cat as string) || defaultCatId,
    set: (val) => {
      // Optionnel : On réinitialise les sous-catégories si on change de catégorie parente
      updateQuery({ cat: val, subcats: undefined })
    },
  })

  // --- SOUS-CATEGORIES ---
  // On les stocke souvent en CSV dans l'URL (ex: ?subcats=12,45,67)
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

  // --- ONBOARDING (On peut garder useState car c'est purement UI/Session) ---
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

function toISODate(d: Date) {
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd}`
}
