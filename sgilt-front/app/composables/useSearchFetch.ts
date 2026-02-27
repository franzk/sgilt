import { useDebounceFn } from '@vueuse/core'

type Category = 'ALL' | 'MUSIC' | 'FOOD' | 'PHOTO' | 'VENUE'

export type ProviderCard = {
  id: string
  name: string
  shortDescription: string
  coverUrl: string
  category: Category
  subcats: string[]
}

export type SearchResponse = {
  results: ProviderCard[]
  countsByCategory: Record<Category, number>
  subcatCounts: Record<string, number> // for active category view
}

export function useSearchFetch() {
  const loading = ref(false)
  const error = ref<string | null>(null)

  const results = ref<ProviderCard[]>([])
  const countsByCategory = ref<Record<Category, number>>({
    ALL: 0,
    MUSIC: 0,
    FOOD: 0,
    PHOTO: 0,
    VENUE: 0,
  })
  const subcatCounts = ref<Record<string, number>>({})

  // Abort + anti-race
  let controller: AbortController | null = null
  let requestSeq = 0

  async function fetchNow(params: { date: string; category: Category; subcats: string[] }) {
    const seq = ++requestSeq

    // Abort previous
    controller?.abort()
    controller = new AbortController()

    loading.value = true
    error.value = null

    try {
      // Replace with your real API endpoint
      const data = await $fetch<SearchResponse>('/api/search', {
        method: 'GET',
        params: {
          date: params.date,
          category: params.category,
          subcats: params.subcats.join(','),
        },
        signal: controller.signal,
      })

      // If a newer request exists, ignore this one
      if (seq !== requestSeq) return

      results.value = data.results
      countsByCategory.value = data.countsByCategory
      subcatCounts.value = data.subcatCounts
    } catch (e: any) {
      // Abort is not an error UX-wise
      if (e?.name === 'AbortError') return
      if (seq !== requestSeq) return

      error.value = e?.message ?? 'Erreur réseau'
    } finally {
      // Only end loading if this is the latest request
      if (seq === requestSeq) loading.value = false
    }
  }

  const fetchDebounced = useDebounceFn(fetchNow, 250)

  function cancelInFlight() {
    controller?.abort()
    controller = null
  }

  return {
    loading,
    error,
    results,
    countsByCategory,
    subcatCounts,
    fetchNow,
    fetchDebounced,
    cancelInFlight,
  }
}
