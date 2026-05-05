/**
 * Composable — charge la fiche d'un prestataire par son slug et expose l'état réactif
 */
import { fetchPrestataireBySlug } from './service/prestataireService'
import type { PrestataireDetail } from './domain/PrestataireDetail'

export function usePrestataire(slug: string) {
  const prestataire = ref<PrestataireDetail | null>(null)
  const loading = ref(true)
  const error = ref<unknown>(null)

  onMounted(async () => {
    try {
      prestataire.value = await fetchPrestataireBySlug(slug)
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  })

  return { prestataire, loading, error }
}
