/**
 * Données de démonstration d'un événement avec billetterie, en dur, partagées par la fiche
 * événement (/e/[slug]) et les écrans du parcours d'achat pour qu'ils ne divergent pas. Le vrai
 * contenu viendra de l'Evenement et de sa billetterie côté back, pas encore construits.
 */

// Pictogramme d'un point fort, par clé : la fiche fait la correspondance clé → icône, comme le
// fera une donnée venue du back.
export type TicketingPictoIcon = 'music' | 'goblet' | 'leaf' | 'team'

export interface TicketingPicto {
  icon: TicketingPictoIcon
  label: string
  sublabel: string
}

// Statut de la billetterie, piloté par l'organisateur (le back la ferme d'office une fois
// l'événement passé). « Complet » n'en est pas un : il se déduit des places restantes.
export type TicketingStatus = 'NOT_OPEN' | 'OPEN' | 'CLOSED'

export interface TicketingMockTicketing {
  status: TicketingStatus
  unitPrice: number
  // Nombre de places restantes — viendra du back (disponibilité ET plafond du sélecteur de
  // quantité en découlent tous les deux, pas de champ `available` séparé à faire diverger).
  remainingTickets: number
}

export interface TicketingMockEvent {
  slug: string
  tag: string
  title: string
  subtitle: string
  // Clé R2 (sgilt-r2-mock/storage/bank/taennel.png) — comme un vrai media prestataire/événement,
  // pas un asset public Nuxt.
  heroImage: string
  // Heure locale du lieu, sans fuseau ('YYYY-MM-DDTHH:mm') : les libellés en sont dérivés.
  startsAt: string
  endsAt: string
  venue: string
  city: string
  // Titre de la présentation, au-dessus de la description ; facultatif.
  sectionTitle?: string
  description: string
  // Absente si l'événement n'a pas de billetterie.
  ticketing?: TicketingMockTicketing
  // Avant l'achat (commande).
  importantInfo: string[]
  // Points forts de la fiche événement.
  pictos: TicketingPicto[]
  // Après l'achat (confirmation) — copie distincte de `importantInfo`.
  practicalInfo: string[]
}

export const mockEvent: TicketingMockEvent = {
  slug: 'les-jeudis-du-taennel',
  tag: 'Édition du 12 mars',
  title: 'Les Jeudis du Taennel',
  subtitle: 'Concert & bar éphémère au cœur du vignoble',
  heroImage: 'bank/taennel.png',
  startsAt: '2026-03-12T19:00',
  endsAt: '2026-03-12T23:00',
  venue: 'Le Taennel',
  city: 'Scherwiller (67)',
  sectionTitle: 'Une soirée musicale et conviviale',
  description:
    "Le jeudi, le Taennel se transforme : concert acoustique, bar à vin nature et food-truck sur place. Une soirée conviviale pour démarrer le week-end en avance, en plein cœur du vignoble.",
  ticketing: {
    status: 'OPEN',
    unitPrice: 12,
    remainingTickets: 7,
  },
  importantInfo: [
    'Événement en plein air, places debout.',
    'Aucun billet physique. Présentez votre billet depuis votre smartphone à l’entrée.',
  ],
  pictos: [
    { icon: 'music', label: 'Concert live', sublabel: 'À partir de 20h' },
    { icon: 'goblet', label: 'Vins & restauration', sublabel: 'Sur place' },
    { icon: 'leaf', label: 'Cadre unique', sublabel: 'Au cœur du vignoble' },
    { icon: 'team', label: 'Ambiance conviviale', sublabel: 'Locale et authentique' },
  ],
  practicalInfo: [
    'Événement en plein air, places debout.',
    "Aucun billet physique. Présentez vos billets depuis votre smartphone à l'entrée.",
    "En cas d'empêchement, contactez l'organisateur.",
  ],
}
