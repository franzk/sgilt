/**
 * Couche service — orchestration des appels API prestataire
 */
import {
  searchPrestatairesApi,
  getPrestataireBySlugApi,
  getMaFicheApi,
  patchPrestataireApi,
  putPrestataireMediasApi,
  uploadPrestataireMediaApi,
  submitPrestataireApi,
} from '../api/prestataireApi'
import { mapPrestataireCard, mapPrestataireDetail } from '../mapper/prestataireMapper'
import type { PrestataireSearchResponse } from '../domain/PrestataireSearchResponse'
import type { PrestataireDetail } from '../domain/PrestataireDetail'
import type { PrestataireFieldEntry, PrestataireUpdatePayload } from '../dto/PrestataireUpdatePayload'
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
    return mapPrestataireDetail(await getPrestataireBySlugApi(slug))
  } catch {
    return null
  }
}

export async function fetchMaFiche(): Promise<PrestataireDetail | null> {
  try {
    return mapPrestataireDetail(await getMaFicheApi())
  } catch {
    return null
  }
}

export async function submitPrestataire(): Promise<void> {
  await submitPrestataireApi()
}

/**
 * Sauvegarde un unique champ de la fiche (déclenché au blur, ou rejoué après échec — voir
 * usePrestataire::retrySave). Les autres champs sont absents du payload — le backend traite une
 * clé absente comme `null` (non touché), au même titre qu'une valeur explicitement nulle.
 */
export async function savePrestataireField(id: string, entry: PrestataireFieldEntry): Promise<void> {
  // TS ne relie pas une clé calculée à sa valeur dans un littéral d'objet — le couple est déjà
  // garanti cohérent par le type `PrestataireFieldEntry` du paramètre `entry`.
  const payload = { [entry.field]: entry.value } as Partial<PrestataireUpdatePayload>
  await patchPrestataireApi(id, payload)
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
