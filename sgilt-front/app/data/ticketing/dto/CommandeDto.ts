/**
 * DTO — contrat API des commandes de billets (/commandes)
 */
export interface CreateCommandeRequestDto {
  slug: string
  quantity: number
  firstName: string
  lastName: string
  email: string
}

export interface CommandeDto {
  id: string
  quantity: number
  totalCents: number
  // Fin du blocage des places faute de paiement, heure locale du serveur (yyyy-MM-ddTHH:mm:ss).
  expiresAt: string
  // Secret client du PaymentIntent de la commande, passé à Stripe.js pour confirmer le paiement.
  clientSecret: string
}

// Commande relue (GET /commandes/{id}), pour l'écran de confirmation.
export interface CommandeSummaryDto {
  id: string
  status: 'PENDING' | 'PAID' | 'CANCELLED'
  quantity: number
  unitPriceCents: number
  totalCents: number
}

// Infos de l'événement d'une commande, pour son e-billet : le bandeau de sa page, quand, où, et les
// infos pratiques.
export interface EvenementInfosDto {
  // null : pas saisi, ou pas de page.
  tag: string | null
  subtitle: string | null
  // 'YYYY-MM-DD'
  date: string
  // Heure locale du lieu, sans fuseau ('YYYY-MM-DDTHH:mm') ; null si l'événement n'en a pas.
  startsAt: string | null
  endsAt: string | null
  venue: string | null
  city: string | null
  // Lien Google Maps du lieu ; null si la page n'a pas de localisation (ou pas de page).
  mapsUrl: string | null
  practicalInfo: string[]
}

// E-billet d'une commande payée (GET /commandes/{id}/e-billet).
export interface EBilletDto {
  title: string
  heroImage: string | null
  // Type de l'événement (mariage, evenement_public…), null s'il n'en a pas.
  eventType: string | null
  evenementInfos: EvenementInfosDto
  quantity: number
  // QR présenté à l'entrée, dessiné par le back : PNG en data URI (data:image/png;base64,…).
  qrImage: string
  // Page de l'événement en ligne : sinon, pas de lien vers elle.
  pageOnline: boolean
}
