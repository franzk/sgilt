import {
  fetchProReservationDetail,
  markDemandeContacted,
  confirmReservation,
  refuseReservation,
  cancelReservationByPro,
} from './service/reservationService'
import type { ProReservationDetail } from './domain/ProReservationDetail'

export function useProReservationDetail(reservationId: string) {
  const { isAuthenticated } = useKeycloak()
  const reservation = ref<ProReservationDetail | null>(null)
  const loading = ref(true)
  const error = ref<unknown>(null)

  async function load() {
    loading.value = true
    try {
      reservation.value = await fetchProReservationDetail(reservationId)
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  }

  watch(isAuthenticated, (authenticated) => {
    if (authenticated) load()
  }, { immediate: true })

  async function markContacted() {
    await markDemandeContacted(reservationId)
    if (reservation.value) reservation.value.status = 'en_discussion'
  }

  async function confirm() {
    await confirmReservation(reservationId)
    if (reservation.value) reservation.value.status = 'confirmee'
  }

  async function refuse(reason: string) {
    await refuseReservation(reservationId, reason)
    if (reservation.value) reservation.value.status = 'refusee'
  }

  async function cancelByPro() {
    await cancelReservationByPro(reservationId)
    if (reservation.value) reservation.value.status = 'annulee'
  }

  return { reservation, loading, error, markContacted, confirm, refuse, cancelByPro }
}
