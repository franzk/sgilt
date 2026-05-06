/**
 * Couche service — orchestration des appels API evenement
 */
import { getEvenementsApi } from '../api/evenementApi'
import { mapEvenementSummary } from '../mapper/evenementMapper'
import type { EventSummary } from '../domain/EventSummary'

export async function getEvenements(): Promise<EventSummary[]> {
  const dtos = await getEvenementsApi()
  return dtos.map(mapEvenementSummary)
}
