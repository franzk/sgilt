/**
 * Chemins des images par défaut.
 */
export const BANK_IMAGE_PATHS: Record<string, string> = {
  mariage: 'bank/mariage.jpg',
  anniversaire: 'bank/anniversaire.jpg',
  soiree_privee: 'bank/soiree_privee.jpg',
  fete_entreprise: 'bank/fete_entreprise.jpg',
  evenement_public: 'bank/evenement_public.jpg',
  autre: 'bank/autre.jpg',
}

/**
 * Chemin de la couverture par défaut d'un type d'événement (banque d'images).
 * Type inconnu ou absent : image « autre ».
 */
export function defaultCoverPath(eventType: string | null | undefined): string {
  return BANK_IMAGE_PATHS[eventType ?? ''] ?? BANK_IMAGE_PATHS.autre!
}
