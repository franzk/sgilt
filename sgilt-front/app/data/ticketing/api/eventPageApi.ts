/**
 * Couche API — appels HTTP bruts vers /eventpages, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { EventPageDto } from '../dto/EventPageDto'

export async function getEventPageBySlugApi(slug: string): Promise<EventPageDto> {
  return apiFetch<EventPageDto>(`/eventpages/${slug}`)
}
