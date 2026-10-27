/**
 * DTO — contrat API de la page publique d'un événement (/e/[slug]), avec sa billetterie
 */
export interface EventPageTicketingDto {
  status: 'NOT_OPEN' | 'OPEN' | 'CLOSED'
  unitPriceCents: number
  remainingTickets: number
}

export interface EventPagePictoDto {
  icon: 'music' | 'goblet' | 'leaf' | 'team'
  label: string
  sublabel: string
}

export interface EventPageDto {
  slug: string
  tag: string | null
  title: string
  subtitle: string | null
  heroImage: string | null
  // 'YYYY-MM-DD'
  date: string
  // Heure locale du lieu, sans fuseau ('YYYY-MM-DDTHH:mm') ; null si l'événement n'en a pas.
  startsAt: string | null
  endsAt: string | null
  venue: string
  city: string
  sectionTitle: string | null
  description: string | null
  ticketing: EventPageTicketingDto | null
  importantInfo: string[]
  pictos: EventPagePictoDto[]
  practicalInfo: string[]
}
