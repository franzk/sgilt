/**
 * Composable — état singleton de la génération IA de fiche du prestataire courant.
 * Le ref vit au niveau module : l'état survit à la navigation entre les onglets de la page
 * d'édition. `loadState` relit l'état depuis le serveur à chaque montage de l'onglet IA, pour
 * refléter une génération existante sans dépendre d'une persistance côté client.
 */
import { FetchError } from 'ofetch'
import { usePrestataire } from '~/data/prestataire/usePrestataire'
import { mapPrestataireDetail } from '~/data/prestataire/mapper/prestataireMapper'
import { generateFicheIaApi, getFicheIaStateApi, applyFicheIaApi } from './api/ficheIaApi'
import type { FicheIaGenerationContentDto } from './dto/FicheIaGenerationDto'
import type { FicheIaSection, FicheIaAction } from './dto/FicheIaApplyDto'

export type FicheIaGenerationError = 'quota' | 'technical'

/** Fenêtre après une génération pendant laquelle les blocs de résultat s'ouvrent par défaut. */
const JUST_GENERATED_WINDOW_MS = 60_000

const result = ref<FicheIaGenerationContentDto | null>(null)
const triesLeft = ref<number | null>(null)
const lastGenerationDateTime = ref<string | null>(null)
const generating = ref(false)
const generationError = ref<FicheIaGenerationError | null>(null)
const loadingState = ref(false)
/** Clé `"SECTION:ACTION"` de l'appel apply en cours, ou `'ALL'` pour l'écrasement total. */
const applying = ref<string | null>(null)
const applyError = ref(false)

const justGenerated = computed(() => {
  if (!lastGenerationDateTime.value) return false
  return Date.now() - new Date(lastGenerationDateTime.value).getTime() < JUST_GENERATED_WINDOW_MS
})

export function useFicheIa() {
  /**
   * Lance une génération IA de fiche à partir de l'URL fournie. En cas d'échec, distingue le
   * quota épuisé (429) de l'échec technique (503/422, réessayable) — les deux derniers sont
   * confondus côté UI, l'endpoint ne renvoyant aucun corps d'erreur exploitable.
   *
   * @param url l'URL du site du prestataire à analyser
   */
  async function generate(url: string): Promise<void> {
    generating.value = true
    generationError.value = null
    try {
      const dto = await generateFicheIaApi(url)
      result.value = dto.result
      triesLeft.value = dto.triesLeft
      lastGenerationDateTime.value = dto.lastGenerationDateTime
    } catch (e: unknown) {
      generationError.value = e instanceof FetchError && e.statusCode === 429 ? 'quota' : 'technical'
    } finally {
      generating.value = false
    }
  }

  /** Charge l'état courant depuis le serveur (résultat, quota, date de dernière génération). */
  async function loadState(): Promise<void> {
    loadingState.value = true
    try {
      const dto = await getFicheIaStateApi()
      result.value = dto.result
      triesLeft.value = dto.triesLeft
      lastGenerationDateTime.value = dto.lastGenerationDateTime
    } finally {
      loadingState.value = false
    }
  }

  /**
   * Applique une section du contenu généré à la fiche réelle et met à jour le ref partagé
   * `prestataire` avec la fiche renvoyée par le serveur.
   */
  async function applyOne(section: FicheIaSection, action: FicheIaAction): Promise<void> {
    applying.value = `${section}:${action}`
    applyError.value = false
    try {
      const dto = await applyFicheIaApi({ section, action })
      usePrestataire().prestataire.value = mapPrestataireDetail(dto)
    } catch {
      applyError.value = true
    } finally {
      applying.value = null
    }
  }

  /** Écrase intégralement la fiche réelle avec le contenu généré (8 sections en une fois). */
  async function applyAll(): Promise<void> {
    applying.value = 'ALL'
    applyError.value = false
    try {
      const dto = await applyFicheIaApi({ section: null, action: 'ECRASER_TOUT' })
      usePrestataire().prestataire.value = mapPrestataireDetail(dto)
    } catch {
      applyError.value = true
    } finally {
      applying.value = null
    }
  }

  return {
    result,
    triesLeft,
    lastGenerationDateTime,
    justGenerated,
    generating,
    generationError,
    loadingState,
    applying,
    applyError,
    generate,
    loadState,
    applyOne,
    applyAll,
  }
}
