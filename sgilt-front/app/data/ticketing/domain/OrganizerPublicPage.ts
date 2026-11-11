/**
 * Domaine — page publique d'un événement administrée par son organisateur dans son board : son
 * contenu, son état (brouillon, publiée), sa billetterie et ses ventes
 */
import type { PublicEventPicto, TicketingStatus } from '~/data/ticketing/domain/PublicEvent'

// État de la page publique, porté par l'événement (absent si le back-office ne l'a pas activée).
export type PublicPageStatus = 'NOT_CREATED' | 'DRAFT' | 'PUBLISHED'

// Champs texte tels que saisis : '' pour un champ vide.
export interface PageContentForm {
  slug: string
  tag: string
  subtitle: string
  sectionTitle: string
  description: string
  importantInfo: string[]
  practicalInfo: string[]
  pictos: PublicEventPicto[]
  // Lien de partage Google Maps, adresse ou point GPS du lieu.
  localisation: string
}

export interface OrganizerPage {
  content: PageContentForm
  published: boolean
  // La page a déjà été publiée : son slug ne change plus.
  slugLocked: boolean
}

export interface TicketingSettings {
  status: TicketingStatus
  // En centimes, comme la fiche.
  unitPriceCents: number
  // Mention affichée après « par place » ; '' sans mention.
  priceNote: string
  capacity: number
}

export interface TicketingSales {
  // Places des commandes payées.
  soldTickets: number
  // Places bloquées par un paiement en cours.
  heldTickets: number
  capacity: number
}

export interface OrganizerPublicPage {
  billetterieEnabled: boolean
  // Absente tant que l'organisateur n'a pas enregistré de contenu.
  page?: OrganizerPage
  // Absentes si la billetterie est désactivée ou pas encore créée.
  ticketing?: TicketingSettings
  sales?: TicketingSales
}
