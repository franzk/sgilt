/**
 * DTO — contrat API de la page publique d'un événement administrée par son organisateur
 * (/user/events/{id}/page-publique) : lue en entier, et renvoyée en entier par chaque modification
 */
import type { PublicEventPicto, TicketingStatus } from '~/data/ticketing/domain/PublicEvent'

export interface PageContentDto {
  slug: string
  tag: string | null
  subtitle: string | null
  sectionTitle: string | null
  description: string | null
  importantInfo: string[]
  practicalInfo: string[]
  pictos: PublicEventPicto[]
}

export interface OrganizerPageDto {
  content: PageContentDto
  published: boolean
  // La page a déjà été publiée : son slug ne change plus.
  slugLocked: boolean
}

export interface TicketingSettingsDto {
  status: TicketingStatus
  unitPriceCents: number
  capacity: number
}

export interface TicketingSalesDto {
  soldTickets: number
  heldTickets: number
  capacity: number
}

export interface OrganizerPublicPageDto {
  billetterieEnabled: boolean
  // null tant que l'organisateur n'a pas enregistré de contenu.
  page: OrganizerPageDto | null
  // null si la billetterie est désactivée ou pas encore créée ; de même pour les ventes.
  ticketing: TicketingSettingsDto | null
  sales: TicketingSalesDto | null
}
