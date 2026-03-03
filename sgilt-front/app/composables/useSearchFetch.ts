import { useThrottleFn } from '@vueuse/core'
import { SearchMockService } from '~/services/search.mock'
import type { PrestataireCardDetail } from '~/types/prestataire'

export function useSearchFetch() {
  const { date, categoryId, currentSubcats } = useSearchUi()

  const loading = ref(false)
  const results = ref<PrestataireCardDetail[]>([])
  const countsByCategory = ref<Record<string, number>>({})
  const subcatCounts = ref<Record<string, number>>({})
  const error = ref<string | null>(null)

  const fetchNow = async () => {
    loading.value = true
    error.value = null

    try {
      const activeSubcats = currentSubcats.value

      const data = await SearchMockService.search({
        date: date.value,
        categoryId: categoryId.value,
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

  const fetchThrottled = useThrottleFn(fetchNow, 300)

  // Watcher auto-pilote
  watch([date, categoryId, currentSubcats], () => fetchThrottled(), {
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
