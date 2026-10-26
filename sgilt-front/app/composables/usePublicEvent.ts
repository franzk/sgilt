/**
 * Composable — événement public (fiche /e/[slug] et parcours d'achat de billets), avec sa
 * billetterie. Seul point d'accès à ces données : chargé une fois par slug, puis partagé entre le
 * middleware `ticketing`, la fiche et les écrans d'achat (useState, transmis du rendu serveur au
 * client). Renvoie pour l'instant les données de démonstration : le branchement sur le back ne
 * touchera que fetchPublicEvent.
 */
import { mockEvent, type TicketingMockEvent } from '~/data/ticketing/mockEvent'

// À remplacer par l'appel au back (événement public par slug) ; null si le slug est inconnu.
async function fetchPublicEvent(_slug: string): Promise<TicketingMockEvent | null> {
  return mockEvent
}

// null si le slug est inconnu : au middleware d'en décider.
export async function loadPublicEvent(slug: string): Promise<TicketingMockEvent | null> {
  const event = useState<TicketingMockEvent | null | undefined>(`public-event-${slug}`)
  if (event.value === undefined) event.value = await fetchPublicEvent(slug)
  return event.value
}

// Pour les pages : un slug inconnu est une 404.
export async function usePublicEvent(slug: string): Promise<TicketingMockEvent> {
  const event = await loadPublicEvent(slug)
  if (!event) throw createError({ statusCode: 404 })
  return event
}
