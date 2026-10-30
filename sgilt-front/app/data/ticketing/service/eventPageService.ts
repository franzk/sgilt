/**
 * Couche service — orchestration des appels API de la page publique d'un événement
 */
import { getEventPageBySlugApi } from '../api/eventPageApi'
import { mapPublicEvent } from '../mapper/eventPageMapper'
import type { PublicEvent } from '../domain/PublicEvent'

export async function fetchPublicEvent(slug: string): Promise<PublicEvent | null> {
  try {
    return mapPublicEvent(await getEventPageBySlugApi(slug))
  } catch {
    return null
  }
}
