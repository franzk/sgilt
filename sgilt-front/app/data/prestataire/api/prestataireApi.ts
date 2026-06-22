/**
 * Couche API — appels HTTP bruts vers /prestataires, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { PrestataireDetailDto } from '../dto/PrestataireDetailDto'
import type { PrestataireSearchResponseDto } from '../dto/PrestataireSearchResponseDto'

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
