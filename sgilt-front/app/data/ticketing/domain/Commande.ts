/**
 * Domaine — commande de billets créée côté back, en attente de paiement : ses places sont
 * bloquées jusqu'à expiresAt
 */
export interface Commande {
  id: string
  quantity: number
  totalCents: number
  expiresAt: string
  // Secret client du PaymentIntent Stripe, pour confirmer le paiement depuis le widget.
  clientSecret: string
}

// Statut d'une commande : PENDING tant que le paiement n'est pas confirmé par Stripe (webhook),
// puis PAID. CANCELLED n'est pas utilisé pour l'instant.
export type CommandeStatus = 'PENDING' | 'PAID' | 'CANCELLED'

// Commande relue depuis le back, pour l'écran de confirmation.
export interface CommandeSummary {
  id: string
  status: CommandeStatus
  quantity: number
  unitPriceCents: number
  totalCents: number
}

// Motif d'échec de la création d'une commande (suffixe de la clé i18n ticketing.payment.errors.*) :
// plus assez de places, billetterie plus ouverte (ou événement introuvable), erreur technique.
export type CommandeFailure = 'NOT_ENOUGH_TICKETS' | 'UNAVAILABLE' | 'TECHNICAL'
