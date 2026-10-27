/**
 * DTO — contrat API admin des événements : liste, et face publique (horaires, page, billetterie)
 * lue et remplacée d'un bloc par /admin/evenements/{id}/page-publique
 */
import type { PublicEventPictoIcon, TicketingStatus } from '~/data/ticketing/domain/PublicEvent'

export interface AdminEvenementListItemDto {
  id: string
  title: string
  // 'YYYY-MM-DD'
  date: string
  ownerEmail: string
  // Slug de sa page publique, null s'il n'en a pas.
  slug: string | null
}

export interface AdminPagePictoDto {
  icon: PublicEventPictoIcon
  label: string
  sublabel: string
}

export interface AdminPageDto {
  slug: string
  tag: string | null
  subtitle: string | null
  sectionTitle: string | null
  description: string | null
  importantInfo: string[]
  practicalInfo: string[]
  pictos: AdminPagePictoDto[]
}

export interface AdminTicketingDto {
  status: TicketingStatus
  unitPriceCents: number
  capacity: number
}

export interface AdminEventPublicFormDto {
  // 'HH:mm', null si l'événement n'en a pas.
  startTime: string | null
  endTime: string | null
  // null : pas de page (envoyé à null, elle est supprimée).
  page: AdminPageDto | null
  // null : pas de billetterie (envoyé à null, elle est supprimée) ; exige une page.
  ticketing: AdminTicketingDto | null
}
