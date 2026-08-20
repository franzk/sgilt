import {
  fetchReservationMeta,
  cancelReservation as cancelReservationService,
  markDemandeContacted,
  confirmReservation,
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

  async function cancel(reason?: string, isPersonal?: boolean) {
    cancelling.value = true
    try {
      await cancelReservationService(reservationId, reason, isPersonal)
      if (reservation.value) reservation.value = { ...reservation.value, status: 'annulee' }
    } finally {
      cancelling.value = false
    }
  }

  const declaringContacted = ref(false)

  async function declareContacted() {
    declaringContacted.value = true
    try {
      await markDemandeContacted(reservationId)
      if (reservation.value) reservation.value = { ...reservation.value, status: 'en_discussion' }
    } finally {
      declaringContacted.value = false
    }
  }

  const confirming = ref(false)

  async function confirm() {
    confirming.value = true
    try {
      await confirmReservation(reservationId)
      if (reservation.value) reservation.value = { ...reservation.value, status: 'confirmee' }
    } finally {
      confirming.value = false
    }
  }

  const canCancel = computed(() =>
    ['nouvelle', 'en_discussion', 'confirmee'].includes(reservation.value?.status ?? ''),
  )
  const canDeclareContacted = computed(() => reservation.value?.status === 'nouvelle')
  const canConfirm = computed(() => reservation.value?.status === 'en_discussion')

  return {
    reservation,
    pending,
    error,
    cancelling,
    canCancel,
    cancel,
    declaringContacted,
    canDeclareContacted,
    declareContacted,
    confirming,
    canConfirm,
    confirm,
  }
}
