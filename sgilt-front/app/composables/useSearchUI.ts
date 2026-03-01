// app/composables/useSearchUI.ts
import { APP_CATEGORIES } from '~/utils/constants'

export function useSearchUi() {
  // Par défaut, on prend l'ID de la première catégorie de tes constantes (souvent "Tous")
  const defaultCatId = APP_CATEGORIES[0]?.id || '1'

  const date = useState<string>('search:date', () => toISODate(new Date()))

  const dateModel = computed({
    get: () => new Date(date.value),
    set: (value: Date) => (date.value = toISODate(value)),
  })

  const categoryId = useState<string>('search:categoryId', () => defaultCatId)

  // On utilise un Record<string, string[]> où la clé est l'ID de la catégorie
  const subcatsByCat = useState<Record<string, string[]>>('search:subcatsByCat', () => ({}))

  // Cookies pour la persistance
  const dateCookie = useCookie<string>('search_date', { default: () => date.value })
  const catCookie = useCookie<string>('search_cat', { default: () => categoryId.value })

  if (import.meta.client) {
    if (dateCookie.value) date.value = String(dateCookie.value)
    if (catCookie.value) categoryId.value = String(catCookie.value)

    watch(date, (v) => (dateCookie.value = v))
    watch(categoryId, (v) => (catCookie.value = v))
  }

  function toggleSubcat(subcatId: string) {
    const current = subcatsByCat.value[categoryId.value] ?? []
    const exists = current.includes(subcatId)
    subcatsByCat.value[categoryId.value] = exists
      ? current.filter((id) => id !== subcatId)
      : [...current, subcatId]
  }

  return {
    date,
    dateModel,
    categoryId,
    subcatsByCat,
    toggleSubcat,
    resetSubcats: () => {
      subcatsByCat.value[categoryId.value] = []
    },
  }
}

function toISODate(d: Date) {
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd}`
}
