/**
 * DTO — contrat API admin des événements : liste, et interrupteurs (page publique, billetterie) de
 * /admin/evenements/{id}/fonctionnalites
 */

export interface AdminEvenementListItemDto {
  id: string
  title: string
  // 'YYYY-MM-DD'
  date: string
  ownerEmail: string
  // Slug de sa page publique, null s'il n'en a pas.
  slug: string | null
  pagePubliqueEnabled: boolean
  billetterieEnabled: boolean
}

// Interrupteurs : pas de billetterie sans page publique (400) ; couper une billetterie qui a des
// ventes est refusé (409).
export interface AdminEvenementFeaturesDto {
  pagePubliqueEnabled: boolean
  billetterieEnabled: boolean
}
