import type { EventDetail } from '~/data/evenement/domain/EventDetail'

/**
 * ImageIds fixes de la banque d'images (servis par le back via /images/bank/*).
 * Les fichiers correspondants sont dans sgilt-core/image-storage/bank/.
 */
export const BANK_IMAGE_IDS: Record<string, string> = {
  mariage: 'bank/mariage',
  anniversaire: 'bank/anniversaire',
  soiree_privee: 'bank/soiree_privee',
  fete_entreprise: 'bank/fete_entreprise',
  autre: 'bank/autre',
}

/**
 * Retourne l'URL de couverture à afficher pour un événement.
 * Utilise la couverture personnalisée si elle existe, sinon la banque d'images selon le type.
 *
 * @param event   les champs coverImage et eventType de l'événement
 * @param toUrl   fonction de construction d'URL depuis un imageId (issue de useImageUrl())
 */
export function resolveEventCover(
  event: Pick<EventDetail, 'coverImage' | 'eventType'>,
  toUrl: (imageId: string) => string,
): string {
  if (event.coverImage) return event.coverImage
  const imageId = BANK_IMAGE_IDS[event.eventType ?? ''] ?? BANK_IMAGE_IDS.autre!
  return toUrl(imageId)
}
