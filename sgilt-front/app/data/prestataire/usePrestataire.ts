/**
 * Composable — état singleton du prestataire courant.
 * Le ref vit au niveau module : une seule instance partagée par tous les consommateurs.
 * Appeler usePrestataire(slug) depuis la page déclenche le chargement et réécrit le ref.
 */
import { fetchPrestataireBySlug } from './service/prestataireService'
import type { PrestataireDetail } from './domain/PrestataireDetail'

const prestataire = ref<PrestataireDetail | null>(null)
const loading = ref(false)
const error = ref<unknown>(null)

export function usePrestataire(slug?: string) {
  if (slug !== undefined) {
    onMounted(async () => {
      prestataire.value = null
      loading.value = true
      error.value = null
      try {
        prestataire.value = await fetchPrestataireBySlug(slug)
      } catch (e) {
        error.value = e
      } finally {
        loading.value = false
      }
    })
  }

  return { prestataire, loading, error }
}
