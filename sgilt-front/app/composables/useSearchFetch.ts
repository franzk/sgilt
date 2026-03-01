import { useDebounceFn } from '@vueuse/core'
import { SearchMockService } from '~/services/search.mock'
import type { PrestataireCardDetail } from '~/types/prestataire'

export function useSearchFetch() {
  const { date, categoryId, subcatsByCat } = useSearchUi()

  const loading = ref(false)
  const results = ref<PrestataireCardDetail[]>([])
  const countsByCategory = ref<Record<string, number>>({})
  const subcatCounts = ref<Record<string, number>>({})
  const error = ref<string | null>(null)

  const fetchNow = async () => {
    console.log('Fetching with params:', {
      date: date.value,
      categoryId: categoryId.value,
      subcats: subcatsByCat.value[categoryId.value] ?? [],
    })
    loading.value = true
    error.value = null

    try {
      const activeSubcats = subcatsByCat.value[categoryId.value] ?? []

      const data = await SearchMockService.search({
        date: date.value,
        categoryId: String(categoryId.value),
        subcats: activeSubcats,
      })

      results.value = data.results
      countsByCategory.value = data.countsByCategory
      subcatCounts.value = data.subcatCounts
    } catch (e) {
      error.value = 'Une erreur est survenue lors de la recherche.'
      console.error(e)
    } finally {
      loading.value = false
    }
  }

  const fetchDebounced = useDebounceFn(fetchNow, 300)

  // Watcher auto-pilote
  watch([date, categoryId, () => subcatsByCat.value[categoryId.value]], () => fetchDebounced(), {
    deep: true,
    immediate: true,
  })

  return {
    loading,
    results,
    countsByCategory,
    subcatCounts,
    error,
    refresh: fetchNow,
  }
}
