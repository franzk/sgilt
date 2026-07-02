import {
  fetchReservationMeta,
  cancelReservation as cancelReservationService,
} from './service/reservationService'
import type { ReservationMeta } from './domain/ReservationMeta'

export function useReservation(reservationId: string) {
  const reservation = ref<ReservationMeta | null>(null)
  const pending = ref(true)
  const error = ref<unknown>(null)

  onMounted(async () => {
    try {
      reservation.value = await fetchReservationMeta(reservationId)
    } catch (e) {
      error.value = e
    } finally {
      pending.value = false
    }
  })

  const cancelling = ref(false)

  async function cancel() {
    cancelling.value = true
    try {
      await cancelReservationService(reservationId)
      if (reservation.value) reservation.value = { ...reservation.value, status: 'annulee' }
    } finally {
      cancelling.value = false
    }
  }

  const canCancel = computed(() =>
    ['nouvelle', 'en_discussion'].includes(reservation.value?.status ?? ''),
  )

  return { reservation, pending, error, cancelling, canCancel, cancel }
}
