// app/composables/useSearchFetch.ts
import { useThrottleFn } from '@vueuse/core'
import { searchPrestataires } from '~/data/prestataire/service/prestataireService'
import type { PrestataireCardDetail } from '~/data/prestataire/domain/PrestataireCardDetail'

export function useSearchFetch() {
  const { categoryKey, currentSubcats } = useSearchUi()

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

  // Recherche côté client uniquement : les résultats sont dynamiques, jamais rendus en SSR.
  onMounted(() => fetchNow())
  watch([categoryKey, currentSubcats], () => fetchThrottled())

  return {
    loading,
    results,
    countsByCategory,
    subcatCounts,
    error,
    refresh: fetchNow,
  }
}
