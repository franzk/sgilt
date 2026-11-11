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
// plus assez de places, billetterie plus ouverte (ou événement introuvable), trop de tentatives
// (limite de débit de la gateway), erreur technique.
export type CommandeFailure = 'NOT_ENOUGH_TICKETS' | 'UNAVAILABLE' | 'TOO_MANY_ATTEMPTS' | 'TECHNICAL'

// Infos de l'événement, montrées sur l'e-billet.
export interface EBilletEvenementInfos {
  tag?: string
  subtitle?: string
  // 'YYYY-MM-DD'
  date: string
  // Heure locale du lieu, sans fuseau ('YYYY-MM-DDTHH:mm') ; absente si l'événement n'en a pas.
  startsAt?: string
  endsAt?: string
  // Lieu et ville : facultatifs sur l'événement.
  venue?: string
  city?: string
  practicalInfo: string[]
}

// E-billet d'une commande payée : un seul QR, qui fait entrer toutes ses places.
export interface EBillet {
  title: string
  // Clé R2, ou l'image par défaut de son type d'événement.
  heroImage: string
  evenementInfos: EBilletEvenementInfos
  quantity: number
  // PNG en data URI, affichable tel quel dans un <img>.
  qrImage: string
  // Page de l'événement en ligne : sinon, pas de lien vers elle.
  pageOnline: boolean
}
