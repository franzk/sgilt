/**
 * Composable — catalogue des clés d'engagement (singleton).
 * Chargé à la demande (enterEdit), jamais sur la fiche publique.
 */
import { getEngagementKeysApi } from './api/prestataireApi'

const catalogue = ref<string[]>([])
const loading = ref(false)

export function useEngagementCatalogue() {
  async function load() {
    if (catalogue.value.length > 0 || loading.value) return
    loading.value = true
    try {
      catalogue.value = await getEngagementKeysApi()
    } finally {
      loading.value = false
    }
  }

  return { catalogue, loading, load }
}
