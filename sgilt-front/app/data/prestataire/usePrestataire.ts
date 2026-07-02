/**
 * Composable — état singleton du prestataire courant.
 * Le ref vit au niveau module : une seule instance partagée par tous les consommateurs.
 * Appeler usePrestataire(slug) depuis la page déclenche le chargement et réécrit le ref.
 */
import { fetchPrestataireBySlug, savePrestataireUpdate } from './service/prestataireService'
import type { PrestataireDetail } from './domain/PrestataireDetail'

const prestataire = ref<PrestataireDetail | null>(null)
const loading = ref(false)
const error = ref<unknown>(null)
const saving = ref(false)
const saved = ref(false)
const saveError = ref(false)

watch(
  prestataire,
  () => {
    if (saved.value) saved.value = false
  },
  { deep: true },
)

export function usePrestataire(slug?: string) {
  if (slug !== undefined) {
    loading.value = true
    onMounted(() => load(slug))
  }

  async function load(s: string) {
    prestataire.value = null
    loading.value = true
    error.value = null
    try {
      prestataire.value = await fetchPrestataireBySlug(s)
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  }

  async function save() {
    if (!prestataire.value) return
    saving.value = true
    saved.value = false
    saveError.value = false
    try {
      await savePrestataireUpdate(prestataire.value)
      saved.value = true
    } catch {
      saveError.value = true
    } finally {
      saving.value = false
    }
  }

  return { prestataire, loading, error, load, save, saving, saved, saveError }
}
