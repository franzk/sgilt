/**
 * Couche service — règles des templates d'événement, appliquées par le back
 */
import { getNewEventRubriquesApi, getRubriqueContenuApi } from '../api/templateApi'
import { mapRubrique, mapRubriqueContenu } from '../mapper/templateMapper'
import type { RubriqueContenu } from '../domain/RubriqueContenu'
import type { EventRubrique } from '~/data/evenement/domain/EventRubrique'

/**
 * Rubriques d'un nouvel événement du type donné, dans l'ordre d'affichage.
 */
export async function fetchNewEventRubriques(eventType: string): Promise<EventRubrique[]> {
  return (await getNewEventRubriquesApi(eventType)).map(mapRubrique)
}

/**
 * Ce que regroupe une rubrique dans un événement du type donné.
 */
export async function fetchRubriqueContenu(
  eventType: string,
  rubriqueKey: string,
): Promise<RubriqueContenu> {
  return mapRubriqueContenu(await getRubriqueContenuApi(eventType, rubriqueKey))
}
