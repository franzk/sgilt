/**
 * Couche service — page publique d'un événement administrée par son organisateur : lecture,
 * aperçu, contenu, publication, billetterie et ses commandes. Chaque modification renvoie toute la page publique,
 * à jour.
 */
import { FetchError } from 'ofetch'
import {
  getOrganizerCommandesApi,
  getOrganizerPagePreviewApi,
  getOrganizerPublicPageApi,
  publishOrganizerPageApi,
  putOrganizerPageContentApi,
  putOrganizerTicketingApi,
  unpublishOrganizerPageApi,
} from '../api/organizerPageApi'
import { mapOrganizerPublicPage, mapPageContentRequest, mapTicketingSettingsRequest } from '../mapper/organizerPageMapper'
import { mapPublicEvent } from '../mapper/eventPageMapper'
import { mapOrganizerCommande } from '../mapper/commandeMapper'
import type { OrganizerPublicPage, PageContentForm, TicketingSettings } from '../domain/OrganizerPublicPage'
import type { PublicEvent } from '../domain/PublicEvent'
import type { OrganizerCommande } from '../domain/Commande'

export async function fetchOrganizerPublicPage(eventId: string): Promise<OrganizerPublicPage> {
  return mapOrganizerPublicPage(await getOrganizerPublicPageApi(eventId))
}

// Page pas encore créée ou page publique désactivée : 404 (voir isPublicPageNotFound).
export async function fetchOrganizerPagePreview(eventId: string): Promise<PublicEvent> {
  return mapPublicEvent(await getOrganizerPagePreviewApi(eventId))
}

// Toutes les commandes de la billetterie, quel que soit leur statut, les plus récentes d'abord.
export async function fetchOrganizerCommandes(eventId: string): Promise<OrganizerCommande[]> {
  return (await getOrganizerCommandesApi(eventId)).map(mapOrganizerCommande)
}

export async function saveOrganizerPageContent(eventId: string, form: PageContentForm): Promise<OrganizerPublicPage> {
  return mapOrganizerPublicPage(await putOrganizerPageContentApi(eventId, mapPageContentRequest(form)))
}

export async function publishOrganizerPage(eventId: string): Promise<OrganizerPublicPage> {
  return mapOrganizerPublicPage(await publishOrganizerPageApi(eventId))
}

export async function unpublishOrganizerPage(eventId: string): Promise<OrganizerPublicPage> {
  return mapOrganizerPublicPage(await unpublishOrganizerPageApi(eventId))
}

export async function saveOrganizerTicketing(eventId: string, settings: TicketingSettings): Promise<OrganizerPublicPage> {
  return mapOrganizerPublicPage(await putOrganizerTicketingApi(eventId, mapTicketingSettingsRequest(settings)))
}

/**
 * Page publique pas (ou plus) activée par le back-office, ou événement inconnu : 404.
 */
export function isPublicPageNotFound(error: unknown): boolean {
  return error instanceof FetchError && error.statusCode === 404
}

/**
 * Traduit l'échec d'un enregistrement du contenu en motif d'erreur (suffixe de clé i18n) : slug
 * déjà pris (400 — le format est vérifié par l'écran), slug verrouillé par une publication (409) ;
 * le reste est technique.
 */
export function toContentFailure(error: unknown): string {
  if (error instanceof FetchError && error.statusCode === 400) return 'SLUG_ALREADY_USED'
  if (error instanceof FetchError && error.statusCode === 409) return 'SLUG_LOCKED'
  return 'TECHNICAL'
}

/**
 * Traduit l'échec d'un enregistrement de la billetterie en motif d'erreur (suffixe de clé i18n) :
 * jauge sous les places vendues (409), ouverture sans page publiée (400 — l'écran le signale
 * avant), billetterie coupée entre-temps par le back-office (404) ; le reste est technique.
 */
export function toTicketingFailure(error: unknown): string {
  if (error instanceof FetchError && error.statusCode === 409) return 'CAPACITY_BELOW_SOLD'
  if (error instanceof FetchError && error.statusCode === 400) return 'OPEN_WITHOUT_PUBLISHED_PAGE'
  if (error instanceof FetchError && error.statusCode === 404) return 'BILLETTERIE_DISABLED'
  return 'TECHNICAL'
}
