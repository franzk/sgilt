import { apiFetch } from '~/composables/useApi'
import type { PrestataireDetailDto, PrestataireSearchResponseDto } from '../dto/PrestataireDto'

export async function searchPrestatairesApi(params: {
  categoryKey?: string
  subcatKey?: string[]
}): Promise<PrestataireSearchResponseDto> {
  return apiFetch<PrestataireSearchResponseDto>('/prestataires', { query: params })
}

export async function getPrestataireBySlugApi(slug: string): Promise<PrestataireDetailDto> {
  return apiFetch<PrestataireDetailDto>(`/prestataires/${slug}`)
}
