/**
 * Domaine — commande de billets créée côté back, en attente de paiement : ses places sont
 * bloquées jusqu'à expiresAt
 */
export interface Commande {
  id: string
  quantity: number
  totalCents: number
  expiresAt: string
}

// Motif d'échec de la création d'une commande (suffixe de la clé i18n ticketing.payment.errors.*) :
// plus assez de places, billetterie plus ouverte (ou événement introuvable), erreur technique.
export type CommandeFailure = 'NOT_ENOUGH_TICKETS' | 'UNAVAILABLE' | 'TECHNICAL'
