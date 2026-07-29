/**
 * DTO — contrat API pour l'application d'une instruction de génération IA à la fiche
 */
export type FicheIaSection =
  | 'BASELINE'
  | 'SHORT_DESCRIPTION'
  | 'IDENTITY'
  | 'BUDGET'
  | 'OFFERINGS'
  | 'TESTIMONIALS'
  | 'DETAILS'
  | 'FAQ'

export type FicheIaAction = 'REMPLACER' | 'AJOUTER' | 'ECRASER_TOUT'

/** Sections dont le contenu est une liste — pilote le choix Remplacer/Ajouter dans la modale d'application. */
export const FICHE_IA_LIST_SECTIONS: readonly FicheIaSection[] = [
  'OFFERINGS',
  'TESTIMONIALS',
  'DETAILS',
  'FAQ',
]

export interface FicheIaApplyRequest {
  section: FicheIaSection | null
  action: FicheIaAction
}
