// app/types/demande.ts

import type { EvenementRequest } from '~/types/evenement'

// Demande à un prestataire : le prestataire visé, le message qui lui est adressé et l'événement
// concerné. Le payload reste plat : les champs de l'événement sont au même niveau.
export interface DemandeRequest extends EvenementRequest {
  prestataireId: string
  prestataireMessage: string | null
}

// Demande d'un visiteur sans compte : les coordonnées s'ajoutent à la demande.
export interface OnboardingDemandeRequest extends DemandeRequest {
  firstName: string
  lastName: string
  email: string
  telephone: string | null
}

// Résumé de l'événement concerné par une demande.
export interface EvenementSummary {
  eventTypeLabel: string | null
  date: Date | undefined
  nbInvites: string
  ville: string
}

// Synthèse d'une demande (prestataire + événement) affichée avant et après l'envoi. Valeurs
// déjà résolues : après l'envoi, l'événement local est vidé, l'écran de confirmation
// travaille sur une copie figée.
export interface DemandeSummary {
  prestataireName: string
  prestataireImage: string
  // Ex. « Restauration · Traiteur ».
  prestataireCategoryLine: string
  evenement: EvenementSummary
}

// Ce qu'affiche l'écran de confirmation après l'envoi (copie figée, en mémoire seulement).
export interface DemandeConfirmation {
  summary: DemandeSummary
  email: string
}

export interface DemandeState {
  prestataireId: string | null
  prestataireName: string
  prestataireImage: string
  prestataireSlug: string
  prenom: string
  nom: string
  email: string
  telephone: string
  prestataireMessage?: string
}

export const ETAPES_COUNT = 6
