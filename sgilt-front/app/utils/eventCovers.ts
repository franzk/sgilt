/**
 * Chemins des images par défaut.
 */
export const BANK_IMAGE_PATHS: Record<string, string> = {
  mariage: 'bank/mariage.jpg',
  anniversaire: 'bank/anniversaire.webp',
  soiree_privee: 'bank/soiree-privee.jpg',
  fete_entreprise: 'bank/fete-entreprise.webp',
  evenement_public: 'bank/evenement-public.webp',
  autre: 'bank/autre.webp',
}

/**
 * Chemin de la couverture par défaut d'un type d'événement (banque d'images).
 * Type inconnu ou absent : image « autre ».
 */
export function defaultCoverPath(eventType: string | null | undefined): string {
  return BANK_IMAGE_PATHS[eventType ?? ''] ?? BANK_IMAGE_PATHS.autre!
}
