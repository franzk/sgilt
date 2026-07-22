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

export interface FicheIaApplyRequest {
  section: FicheIaSection | null
  action: FicheIaAction
}
