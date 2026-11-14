/**
 * Couche API — appels HTTP vers /user/events/{id}/page-publique (page publique d'un événement,
 * administrée par son organisateur)
 */
import { apiFetch } from '~/composables/useApi'
import type { OrganizerPublicPageDto, PageContentDto, TicketingSettingsDto } from '../dto/OrganizerPublicPageDto'
import type { EventPageDto } from '../dto/EventPageDto'
import type { OrganizerCommandeDto } from '../dto/CommandeDto'

const basePath = (eventId: string) => `/user/events/${eventId}/page-publique`

export async function getOrganizerPublicPageApi(eventId: string): Promise<OrganizerPublicPageDto> {
  return apiFetch<OrganizerPublicPageDto>(basePath(eventId))
}

// Aperçu : la page comme sur /e/{slug}, publiée ou non, sans billetterie.
export async function getOrganizerPagePreviewApi(eventId: string): Promise<EventPageDto> {
  return apiFetch<EventPageDto>(`${basePath(eventId)}/apercu`)
}

export async function putOrganizerPageContentApi(
  eventId: string,
  body: PageContentDto,
): Promise<OrganizerPublicPageDto> {
  return apiFetch<OrganizerPublicPageDto>(`${basePath(eventId)}/contenu`, { method: 'PUT', body })
}

export async function publishOrganizerPageApi(eventId: string): Promise<OrganizerPublicPageDto> {
  return apiFetch<OrganizerPublicPageDto>(`${basePath(eventId)}/publier`, { method: 'POST' })
}

export async function unpublishOrganizerPageApi(eventId: string): Promise<OrganizerPublicPageDto> {
  return apiFetch<OrganizerPublicPageDto>(`${basePath(eventId)}/depublier`, { method: 'POST' })
}

export async function putOrganizerTicketingApi(
  eventId: string,
  body: TicketingSettingsDto,
): Promise<OrganizerPublicPageDto> {
  return apiFetch<OrganizerPublicPageDto>(`${basePath(eventId)}/billetterie`, { method: 'PUT', body })
}

// Commandes de la billetterie, les plus récentes d'abord.
export async function getOrganizerCommandesApi(eventId: string): Promise<OrganizerCommandeDto[]> {
  return apiFetch<OrganizerCommandeDto[]>(`${basePath(eventId)}/billetterie/commandes`)
}
