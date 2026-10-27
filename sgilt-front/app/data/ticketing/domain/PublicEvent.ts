/**
 * Domaine — événement public (fiche /e/[slug] et parcours d'achat de billets), avec sa billetterie
 */

// Pictogrammes des points forts, par clé : la fiche fait la correspondance clé → icône, le
// back-office les propose à la saisie.
export const PUBLIC_EVENT_PICTO_ICONS = ['music', 'goblet', 'leaf', 'team'] as const
export type PublicEventPictoIcon = (typeof PUBLIC_EVENT_PICTO_ICONS)[number]

export interface PublicEventPicto {
  icon: PublicEventPictoIcon
  label: string
  sublabel: string
}

// Statut de la billetterie, piloté par l'organisateur (le back la ferme d'office une fois
// l'événement passé). « Complet » n'en est pas un : il se déduit des places restantes.
export type TicketingStatus = 'NOT_OPEN' | 'OPEN' | 'CLOSED'

export interface PublicEventTicketing {
  status: TicketingStatus
  // En centimes : les calculs de montant restent entiers, la conversion en euros se fait à
  // l'affichage.
  unitPriceCents: number
  // Disponibilité ET plafond du sélecteur de quantité en découlent tous les deux, pas de champ
  // `available` séparé à faire diverger.
  remainingTickets: number
}

export interface PublicEvent {
  slug: string
  tag?: string
  title: string
  subtitle?: string
  // Clé R2, comme un media prestataire/événement, pas un asset public Nuxt.
  heroImage?: string
  // 'YYYY-MM-DD' : toujours là, même sans horaires.
  date: string
  // Heure locale du lieu, sans fuseau ('YYYY-MM-DDTHH:mm') ; absente si l'événement n'en a pas.
  // La fin tombe le lendemain si elle passe minuit (calculé par le back).
  startsAt?: string
  endsAt?: string
  venue: string
  city: string
  // Titre de la présentation, au-dessus de la description.
  sectionTitle?: string
  description?: string
  // Absente si l'événement n'a pas de billetterie.
  ticketing?: PublicEventTicketing
  // Avant l'achat (commande).
  importantInfo: string[]
  // Points forts de la fiche événement.
  pictos: PublicEventPicto[]
  // Après l'achat (confirmation) — copie distincte de `importantInfo`.
  practicalInfo: string[]
}
