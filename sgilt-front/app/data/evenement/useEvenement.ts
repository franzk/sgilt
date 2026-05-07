/**
 * Composable — expose les données d'un événement unique (détail, counts, réservations)
 */
import { fetchEventDetail, fetchEventCounts, fetchEventReservations } from './service/evenementService'
import type { EventDetail } from './domain/EventDetail'
import type { EventCounts } from './domain/EventCounts'
import type { ClientContactInfo } from '~/data/reservation/domain/ClientContactInfo'
import type { Reservation } from '~/data/reservation/domain/Reservation'

export function useEventDetail(id: string) {
  const event = ref<EventDetail | null>(null)
  const clientInfo = ref<ClientContactInfo | null>(null)
  const pending = ref(true)
  const error = ref<unknown>(null)

  onMounted(async () => {
    try {
      const result = await fetchEventDetail(id)
      event.value = result.event
      clientInfo.value = result.clientInfo
    } catch (e) {
      error.value = e
    } finally {
      pending.value = false
    }
  })

  return { event, clientInfo, pending, error }
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
  const reservations = ref<Reservation[]>([])
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
