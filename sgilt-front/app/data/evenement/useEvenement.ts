/**
 * Composable — expose les données d'un événement unique (détail, counts, réservations)
 */
import {
  fetchEvent,
  fetchEventCounts,
  fetchEventReservations,
} from './service/evenementService'
import type { EventMeta } from './domain/EventMeta'
import type { EventRubrique } from './domain/EventRubrique'
import type { EventCounts } from './domain/EventCounts'
import type { ClientContactInfo } from '~/data/reservation/domain/ClientContactInfo'
import type { ReservationSummary } from '~/data/reservation/domain/ReservationSummary'

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

export function useEventCounts(id: string) {
  const counts = ref<EventCounts | null>(null)
  const pending = ref(true)
  const error = ref<unknown>(null)

  onMounted(async () => {
    try {
      counts.value = await fetchEventCounts(id)
    } catch (e) {
      error.value = e
    } finally {
      pending.value = false
    }
  })

  return { counts, pending, error }
}

export function useEventReservations(id: string) {
  const reservations = ref<ReservationSummary[]>([])
  const pending = ref(true)
  const error = ref<unknown>(null)

  onMounted(async () => {
    try {
      reservations.value = await fetchEventReservations(id)
    } catch (e) {
      error.value = e
    } finally {
      pending.value = false
    }
  })

  return { reservations, pending, error }
}
