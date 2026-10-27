/**
 * Composable — événement public (fiche /e/[slug] et parcours d'achat de billets), avec sa
 * billetterie. Seul point d'accès à ces données : chargé une fois par slug, puis partagé entre le
 * middleware `ticketing`, la fiche et les écrans d'achat (useState, transmis du rendu serveur au
 * client).
 */
import type { PublicEvent } from '~/data/ticketing/domain/PublicEvent'
import { fetchPublicEvent } from '~/data/ticketing/service/eventPageService'

// null si le slug est inconnu : au middleware d'en décider.
export async function loadPublicEvent(slug: string): Promise<PublicEvent | null> {
  const event = useState<PublicEvent | null | undefined>(`public-event-${slug}`)
  if (event.value === undefined) event.value = await fetchPublicEvent(slug)
  return event.value
}

// Pour les pages : un slug inconnu est une 404.
export async function usePublicEvent(slug: string): Promise<PublicEvent> {
  const event = await loadPublicEvent(slug)
  if (!event) throw createError({ statusCode: 404 })
  return event
}
