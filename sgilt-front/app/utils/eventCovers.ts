import type { EventDetail } from '~/data/evenement/domain/EventDetail'

/**
 * Chemins des images par défaut.
  */
export const BANK_IMAGE_PATHS: Record<string, string> = {
  mariage: 'bank/mariage.jpg',
  anniversaire: 'bank/anniversaire.jpg',
  soiree_privee: 'bank/soiree_privee.jpg',
  fete_entreprise: 'bank/fete_entreprise.jpg',
  autre: 'bank/autre.jpg',
}

/**
 * Retourne l'URL de couverture à afficher pour un événement.
 * Utilise la couverture personnalisée si elle existe, sinon la banque d'images selon le type.
 *
 * @param event   les champs coverImage et eventType de l'événement
 * @param toUrl   fonction de construction d'URL depuis un imagePath (issue de useImageUrl())
 */
export function resolveEventCover(
  event: Pick<EventDetail, 'coverImage' | 'eventType'>,
  toUrl: (imagePath: string) => string,
): string {
  if (event.coverImage) return toUrl(event.coverImage)
  const imagePath = BANK_IMAGE_PATHS[event.eventType ?? ''] ?? BANK_IMAGE_PATHS.autre!
  return toUrl(imagePath)
}
