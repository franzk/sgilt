/**
 * Couche API — appels HTTP bruts vers /templates, sans logique métier
 */
import { apiFetch } from '~/composables/useApi'
import type { RubriqueContenuDto } from '../dto/RubriqueContenuDto'
import type { RubriqueDto } from '../dto/RubriqueDto'

/**
 * Rubriques d'un nouvel événement du type donné, dans l'ordre d'affichage (vide sans template).
 */
export async function getNewEventRubriquesApi(eventType: string): Promise<RubriqueDto[]> {
  return apiFetch<RubriqueDto[]>(`/templates/${encodeURIComponent(eventType)}/rubriques`)
}

/**
 * Ce que regroupe une rubrique dans un événement du type donné (vide si aucun template ne la définit).
 */
export async function getRubriqueContenuApi(
  eventType: string,
  rubriqueKey: string,
): Promise<RubriqueContenuDto> {
  return apiFetch<RubriqueContenuDto>(
    `/templates/${encodeURIComponent(eventType)}/rubriques/${encodeURIComponent(rubriqueKey)}`,
  )
}
