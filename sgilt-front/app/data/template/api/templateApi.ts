/**
 * Couche API — appels HTTP bruts vers /templates, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { RubriqueDto } from '../dto/RubriqueDto'

/**
 * Rubriques d'un nouvel événement du type donné, dans l'ordre d'affichage (vide sans template).
 */
export async function getNewEventRubriquesApi(eventType: string): Promise<RubriqueDto[]> {
  return apiFetch<RubriqueDto[]>(`/templates/${encodeURIComponent(eventType)}/rubriques`)
}
