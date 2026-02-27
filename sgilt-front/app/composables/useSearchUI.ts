type Category = 'ALL' | 'MUSIC' | 'FOOD' | 'PHOTO' | 'VENUE'

type SearchUi = {
  date: Ref<string> // YYYY-MM-DD
  category: Ref<Category>
  subcatsByCat: Ref<Record<string, string[]>>
  resetSubcatsForActiveCat: () => void
  toggleSubcat: (subcat: string) => void
}

export function useSearchUi(): SearchUi {
  const date = useState<string>('search:date', () => toISODate(new Date()))
  const category = useState<Category>('search:category', () => 'ALL')
  const subcatsByCat = useState<Record<string, string[]>>('search:subcatsByCat', () => ({}))

  // cookies (persist refresh)
  const dateCookie = useCookie<string>('sgilt_search_date', {
    default: () => date.value,
    sameSite: 'lax',
    maxAge: 60 * 60 * 24 * 90,
  })
  const catCookie = useCookie<Category>('sgilt_search_cat', {
    default: () => category.value,
    sameSite: 'lax',
    maxAge: 60 * 60 * 24 * 90,
  })

  // Init from cookies on client only
  if (import.meta.client) {
    // If cookie exists, prefer it
    if (dateCookie.value) date.value = dateCookie.value
    if (catCookie.value) category.value = catCookie.value

    // Keep cookies updated
    watch(date, (v) => (dateCookie.value = v))
    watch(category, (v) => (catCookie.value = v))
  }

  function getActiveSubcats(): string[] {
    return subcatsByCat.value[category.value] ?? []
  }

  function setActiveSubcats(next: string[]) {
    subcatsByCat.value = {
      ...subcatsByCat.value,
      [category.value]: next,
    }
  }

  function resetSubcatsForActiveCat() {
    setActiveSubcats([])
  }

  function toggleSubcat(subcat: string) {
    const current = getActiveSubcats()
    const exists = current.includes(subcat)
    setActiveSubcats(exists ? current.filter((s) => s !== subcat) : [...current, subcat])
  }

  return {
    date,
    category,
    subcatsByCat,
    resetSubcatsForActiveCat,
    toggleSubcat,
  }
}

function toISODate(d: Date) {
  const yyyy = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd}`
}

export function addDaysISO(iso: string, delta: number) {
  const [y, m, d] = iso.split('-').map(Number)
  if (!y || isNaN(y) || !m || isNaN(m) || !d || isNaN(d))
    throw new Error(`Invalid ISO date: ${iso}`)
  const dt = new Date(y, m - 1, d)
  dt.setDate(dt.getDate() + delta)
  return toISODate(dt)
}
