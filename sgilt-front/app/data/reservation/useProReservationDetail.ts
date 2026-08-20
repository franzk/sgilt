import {
  fetchProReservationDetail,
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

  watch(
    isAuthenticated,
    (authenticated) => {
      if (authenticated) load()
    },
    { immediate: true },
  )

  async function refuse(reason: string) {
    await refuseReservation(reservationId, reason)
    if (reservation.value) reservation.value.status = 'refusee'
  }

  async function cancelByPro(reason?: string, isPersonal?: boolean) {
    await cancelReservationByPro(reservationId, reason, isPersonal)
    if (reservation.value) reservation.value.status = 'annulee'
  }

  return { reservation, loading, error, refuse, cancelByPro }
}
