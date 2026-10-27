/**
 * Domaine — événements dans le back-office admin : la liste, et le formulaire de leur face
 * publique (horaires, page /e/[slug], billetterie), saisi et enregistré d'un bloc
 */
import type { PublicEventPicto, TicketingStatus } from '~/data/ticketing/domain/PublicEvent'

export interface AdminEvenementListItem {
  id: string
  title: string
  // 'YYYY-MM-DD'
  date: string
  ownerEmail: string
  // Absent si l'événement n'a pas de page publique.
  slug?: string
}

// Champs texte tels que saisis : '' pour un champ vide.
export interface AdminPageForm {
  slug: string
  tag: string
  subtitle: string
  sectionTitle: string
  description: string
  importantInfo: string[]
  practicalInfo: string[]
  pictos: PublicEventPicto[]
}

export interface AdminTicketingForm {
  status: TicketingStatus
  unitPriceCents: number
  capacity: number
}

export interface AdminEventPublicForm {
  // 'HH:mm', '' si l'événement n'en a pas.
  startTime: string
  endTime: string
  // null : l'événement n'a pas de page ; enregistré ainsi, elle est supprimée.
  page: AdminPageForm | null
  // null : l'événement n'a pas de billetterie ; enregistré ainsi, elle est supprimée.
  ticketing: AdminTicketingForm | null
}
