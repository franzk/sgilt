/**
 * Domaine — événements dans le back-office admin : la liste avec leurs interrupteurs (page
 * publique, billetterie)
 */

export interface AdminEvenementListItem {
  id: string
  title: string
  // 'YYYY-MM-DD'
  date: string
  ownerEmail: string
  // Absent si l'événement n'a pas de page publique.
  slug?: string
  features: AdminEvenementFeatures
}

// Interrupteurs du back-office : l'équipe Sgilt active, l'organisateur administre.
export interface AdminEvenementFeatures {
  pagePubliqueEnabled: boolean
  // Jamais sans page publique.
  billetterieEnabled: boolean
}
