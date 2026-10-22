/**
 * Composable — expose les données d'un événement unique (détail, rubriques et leurs réservations)
 */
import { fetchEvent } from './service/evenementService'
import type { EventMeta } from './domain/EventMeta'
import type { EventRubrique } from './domain/EventRubrique'
import type { ClientContactInfo } from '~/data/reservation/domain/ClientContactInfo'

export function useEvent(id: string) {
  const event = ref<EventMeta | null>(null)
  const clientInfo = ref<ClientContactInfo | null>(null)
  const rubriques = ref<EventRubrique[]>([])
  const pending = ref(true)
  const error = ref<unknown>(null)

  // `pending` ne couvre que le premier chargement : un rechargement garde les données affichées.
  async function refresh() {
    try {
      const result = await fetchEvent(id)
      event.value = result.event
      clientInfo.value = result.clientInfo
      rubriques.value = result.rubriques
    } catch (e) {
      error.value = e
    } finally {
      pending.value = false
    }
  }

  onMounted(refresh)

  return { event, clientInfo, rubriques, pending, error, refresh }
}
