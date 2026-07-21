/**
 * Composable — état singleton de la génération IA de fiche du prestataire courant.
 * Le ref vit au niveau module : l'état survit à la navigation entre les onglets de la page
 * d'édition, sans re-fetch (il n'existe pas d'endpoint de lecture du quota / de la dernière
 * génération — seule la réponse de l'appel de génération renseigne triesLeft).
 */
import { FetchError } from 'ofetch'
import { generateFicheIaApi } from './api/ficheIaApi'
import type { FicheIaGenerationContentDto } from './dto/FicheIaGenerationDto'

export type FicheIaGenerationError = 'quota' | 'technical'

const result = ref<FicheIaGenerationContentDto | null>(null)
const triesLeft = ref<number | null>(null)
const generating = ref(false)
const generationError = ref<FicheIaGenerationError | null>(null)

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
    } catch (e: unknown) {
      generationError.value = e instanceof FetchError && e.statusCode === 429 ? 'quota' : 'technical'
    } finally {
      generating.value = false
    }
  }

  return {
    result,
    triesLeft,
    generating,
    generationError,
    generate,
  }
}
