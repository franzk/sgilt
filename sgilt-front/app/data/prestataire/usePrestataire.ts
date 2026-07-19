/**
 * Composable — état singleton du prestataire courant.
 * Le ref vit au niveau module : une seule instance partagée par tous les consommateurs.
 * Appeler usePrestataire(slug) depuis la page déclenche le chargement et réécrit le ref.
 */
import {
  fetchPrestataireBySlug,
  fetchMaFiche,
  savePrestataireField,
  saveMediasUpdate,
  uploadPrestataireMedia,
  submitPrestataire,
} from './service/prestataireService'
import type { PrestataireDetail } from './domain/PrestataireDetail'
import type { PrestataireFieldEntry, PrestataireUpdatePayload } from './dto/PrestataireUpdatePayload'
import type { Media } from './domain/Media'

const prestataire = ref<PrestataireDetail | null>(null)
const loading = ref(false)
const error = ref<unknown>(null)
const saving = ref(false)
const saveError = ref(false)
/** Dernier couple (champ, valeur) dont la sauvegarde a échoué — rejouable via retrySave(). */
const lastFailedSave = ref<PrestataireFieldEntry | null>(null)
const submitting = ref(false)
const submitError = ref(false)

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
    if (!prestataire.value) return
    submitting.value = true
    submitError.value = false
    try {
      await submitPrestataire()
      prestataire.value.status = 'IN_REVIEW'
    } catch {
      submitError.value = true
    } finally {
      submitting.value = false
    }
  }

  /**
   * Sauvegarde un unique champ, déclenché au blur d'un champ éditable. Chaque appel écrase
   * uniquement ce champ en base (voir {@link savePrestataireField}) — jamais un instantané complet.
   */
  async function saveField<K extends keyof PrestataireUpdatePayload>(
    field: K,
    value: PrestataireUpdatePayload[K],
  ): Promise<void> {
    await saveEntry({ field, value })
  }

  /**
   * Rejoue la dernière sauvegarde de champ ayant échoué (bouton "Réessayer" affiché en cas
   * d'erreur). Sans effet si aucune sauvegarde n'est en échec.
   */
  async function retrySave(): Promise<void> {
    if (!lastFailedSave.value) return
    await saveEntry(lastFailedSave.value)
  }

  async function saveEntry(entry: PrestataireFieldEntry): Promise<void> {
    if (!prestataire.value) return
    saving.value = true
    saveError.value = false
    try {
      await savePrestataireField(prestataire.value.id, entry)
      lastFailedSave.value = null
    } catch {
      saveError.value = true
      lastFailedSave.value = entry
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
    saveField,
    retrySave,
    saving,
    saveError,
    saveMedias,
    uploadMedia,
    submit,
    submitting,
    submitError,
  }
}
