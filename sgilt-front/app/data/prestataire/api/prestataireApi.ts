/**
 * Couche API — appels HTTP bruts vers /prestataires, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { PrestataireDetailDto } from '../dto/PrestataireDetailDto'
import type { PrestataireSearchResponseDto } from '../dto/PrestataireSearchResponseDto'
import type { PrestataireUpdatePayload } from '../dto/PrestataireUpdatePayload'

export async function searchPrestatairesApi(params: {
  categoryKey?: string
  subcatKey?: string[]
}): Promise<PrestataireSearchResponseDto> {
  return apiFetch<PrestataireSearchResponseDto>('/prestataires', { query: params })
}

export async function getPrestataireBySlugApi(slug: string): Promise<PrestataireDetailDto> {
  return apiFetch<PrestataireDetailDto>(`/prestataires/${slug}`)
}

export async function getEngagementKeysApi(): Promise<string[]> {
  return apiFetch<string[]>('/prestataires/engagements')
}

export async function patchPrestataireApi(
  id: string,
  payload: PrestataireUpdatePayload,
): Promise<void> {
  return apiFetch<void>(`/prestataires/${id}`, { method: 'PATCH', body: payload })
}

export async function uploadPrestataireMediaApi(file: File): Promise<{ key: string }> {
  const body = new FormData()
  body.append('file', file)
  return apiFetch<{ key: string }>('/prestataires/ma-fiche/medias/upload', { method: 'POST', body })
}
