import { fetchActiveReservations } from './service/reservationService'
import type { ActiveReservations } from './domain/ActiveReservation'

export function useActiveReservations() {
  const data = ref<ActiveReservations | null>(null)
  const loading = ref(true)
  const error = ref<unknown>(null)

  onMounted(async () => {
    try {
      data.value = await fetchActiveReservations()
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  })

  return { data, loading, error }
}
