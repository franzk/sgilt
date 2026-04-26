// app/composables/useSearchFetch.ts
import { useThrottleFn } from '@vueuse/core'
import { searchPrestataires } from '~/data/prestataire/service/prestataireService'
import type { PrestataireCardDetail } from '~/data/prestataire/domain/prestataire'

export function useSearchFetch() {
  const { date, categoryKey, currentSubcats } = useSearchUi()

  const loading = ref(false)
  const results = ref<PrestataireCardDetail[]>([])
  const countsByCategory = ref<Record<string, number>>({})
  const subcatCounts = ref<Record<string, number>>({})
  const error = ref<string | null>(null)

  const fetchNow = async () => {
    loading.value = true
    error.value = null
    try {
      const data = await searchPrestataires({
        date: date.value,
        categoryKey: categoryKey.value,
        subcatKeys: currentSubcats.value,
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

  watch([date, categoryKey, currentSubcats], () => fetchThrottled(), { immediate: true })

  return {
    loading,
    results,
    countsByCategory,
    subcatCounts,
    error,
    refresh: fetchNow,
  }
}
