/**
 * Couche API — appels HTTP bruts vers /prestataires/ma-fiche/generation-ia, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { PrestataireDetailDto } from '~/data/prestataire/dto/PrestataireDetailDto'
import type { FicheIaGenerationResultDto } from '../dto/FicheIaGenerationDto'
import type { FicheIaApplyRequest } from '../dto/FicheIaApplyDto'

export async function generateFicheIaApi(url: string): Promise<FicheIaGenerationResultDto> {
  return apiFetch<FicheIaGenerationResultDto>('/prestataires/ma-fiche/generation-ia', {
    method: 'POST',
    body: { url },
  })
}

export async function getFicheIaStateApi(): Promise<FicheIaGenerationResultDto> {
  return apiFetch<FicheIaGenerationResultDto>('/prestataires/ma-fiche/generation-ia')
}

export async function applyFicheIaApi(request: FicheIaApplyRequest): Promise<PrestataireDetailDto> {
  return apiFetch<PrestataireDetailDto>('/prestataires/ma-fiche/generation-ia', {
    method: 'PATCH',
    body: request,
  })
}
