/**
 * Couche service — orchestration des appels API prestataire
 */
import {
  searchPrestatairesApi,
  getPrestataireBySlugApi,
  patchPrestataireApi,
  putPrestataireMediasApi,
  uploadPrestataireMediaApi,
} from '../api/prestataireApi'
import {
  mapPrestataireCard,
  mapPrestataireDetail,
  mapPrestataireUpdatePayload,
} from '../mapper/prestataireMapper'
import type { PrestataireSearchResponse } from '../domain/PrestataireSearchResponse'
import type { PrestataireDetail } from '../domain/PrestataireDetail'
import type { Media } from '../domain/Media'

export async function searchPrestataires(params: {
  date: string
  categoryKey: string
  subcatKeys: string[]
}): Promise<PrestataireSearchResponse> {
  const isAll = params.categoryKey === 'all' || !params.categoryKey
  const activeSubcats = params.subcatKeys.filter(Boolean)

  const query: { categoryKey?: string; subcatKey?: string[] } = {}
  if (activeSubcats.length > 0) {
    query.subcatKey = activeSubcats
  } else if (!isAll) {
    query.categoryKey = params.categoryKey
  }

  const dto = await searchPrestatairesApi(query)

  return {
    results: dto.results.map(mapPrestataireCard),
    countsByCategory: dto.countsByCategory,
    subcatCounts: dto.subcatCounts,
  }
}

export async function fetchPrestataireBySlug(slug: string): Promise<PrestataireDetail | null> {
  try {
    const dto = await getPrestataireBySlugApi(slug)
    return mapPrestataireDetail(dto)
  } catch {
    return null
  }
}

export async function savePrestataireUpdate(p: PrestataireDetail): Promise<void> {
  await patchPrestataireApi(p.id, mapPrestataireUpdatePayload(p))
}

export async function saveMediasUpdate(medias: Media[]): Promise<PrestataireDetail> {
  const dto = await putPrestataireMediasApi(medias)
  return mapPrestataireDetail(dto)
}

export async function uploadPrestataireMedia(file: File): Promise<{ key: string }> {
  const maxUploadSizeMb = Number(useRuntimeConfig().public.maxUploadSizeMb)
  const resized = await resizeImageForUpload(file, maxUploadSizeMb * 1024 * 1024)
  return uploadPrestataireMediaApi(resized)
}
