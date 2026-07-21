/**
 * Couche API — appels HTTP bruts vers /prestataires/ma-fiche/generation-ia, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { FicheIaGenerationResultDto } from '../dto/FicheIaGenerationDto'

export async function generateFicheIaApi(url: string): Promise<FicheIaGenerationResultDto> {
  return apiFetch<FicheIaGenerationResultDto>('/prestataires/ma-fiche/generation-ia', {
    method: 'POST',
    body: { url },
  })
}
