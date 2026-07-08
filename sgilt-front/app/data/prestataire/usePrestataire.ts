/**
 * Composable — état singleton du prestataire courant.
 * Le ref vit au niveau module : une seule instance partagée par tous les consommateurs.
 * Appeler usePrestataire(slug) depuis la page déclenche le chargement et réécrit le ref.
 */
import {
  fetchPrestataireBySlug,
  fetchMaFiche,
  savePrestataireUpdate,
  saveMediasUpdate,
  uploadPrestataireMedia,
  submitPrestataire,
} from './service/prestataireService'
import type { PrestataireDetail } from './domain/PrestataireDetail'
import type { Media } from './domain/Media'

const prestataire = ref<PrestataireDetail | null>(null)
const loading = ref(false)
const error = ref<unknown>(null)
const saving = ref(false)
const saved = ref(false)
const saveError = ref(false)
const submitting = ref(false)
const submitError = ref(false)

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

  async function loadFrom(fetcher: () => Promise<PrestataireDetail | null>) {
    prestataire.value = null
    loading.value = true
    error.value = null
    try {
      prestataire.value = await fetcher()
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  }

  async function load(s: string) {
    await loadFrom(() => fetchPrestataireBySlug(s))
  }

  async function loadMaFiche() {
    await loadFrom(() => fetchMaFiche())
  }

  async function submit() {
    submitting.value = true
    submitError.value = false
    try {
      await submitPrestataire()
      if (prestataire.value) prestataire.value.status = 'IN_REVIEW'
    } catch {
      submitError.value = true
    } finally {
      submitting.value = false
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

  async function saveMedias(medias: Media[]): Promise<void> {
    prestataire.value = await saveMediasUpdate(medias)
  }

  async function uploadMedia(file: File): Promise<{ key: string }> {
    return uploadPrestataireMedia(file)
  }

  return {
    prestataire,
    loading,
    error,
    load,
    loadMaFiche,
    save,
    saving,
    saved,
    saveError,
    saveMedias,
    uploadMedia,
    submit,
    submitting,
    submitError,
  }
}
