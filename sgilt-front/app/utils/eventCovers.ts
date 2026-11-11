/**
 * Couvertures par défaut des événements (banque d'images), par type d'événement : dérivées de la
 * liste unique des types (utils/eventTypes.ts). Aussi la liste des couvertures proposées dans le
 * choix de la couverture (EventEditDialog.vue).
 */

import { EVENT_TYPES } from '~/utils/eventTypes'

export const BANK_IMAGE_PATHS: Record<string, string> = Object.fromEntries(
  EVENT_TYPES.map((type) => [type.key, type.defaultCover]),
)

/**
 * Chemin de la couverture par défaut d'un type d'événement (banque d'images).
 * Type inconnu ou absent : image « autre ».
 */
export function defaultCoverPath(eventType: string | null | undefined): string {
  return BANK_IMAGE_PATHS[eventType ?? ''] ?? BANK_IMAGE_PATHS.autre!
}
