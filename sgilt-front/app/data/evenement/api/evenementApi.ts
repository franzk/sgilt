/**
 * Couche API — appels HTTP bruts vers /events, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { EvenementSummaryDto } from '../dto/evenementDto'

export async function getEvenementsApi(): Promise<EvenementSummaryDto[]> {
  return apiFetch<EvenementSummaryDto[]>('/events')
}
