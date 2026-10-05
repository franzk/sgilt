/**
 * Couche service — règles des templates d'événement, appliquées par le back
 */
import { getNewEventRubriquesApi } from '../api/templateApi'
import { mapRubrique } from '../mapper/templateMapper'
import type { EventRubrique } from '~/data/evenement/domain/EventRubrique'

/**
 * Rubriques d'un nouvel événement du type donné, dans l'ordre d'affichage.
 */
export async function fetchNewEventRubriques(eventType: string): Promise<EventRubrique[]> {
  return (await getNewEventRubriquesApi(eventType)).map(mapRubrique)
}
