import { fetchProReservations, fetchProBoardCounts } from './service/reservationService'
import type { ProReservationSummary } from './domain/ProReservationSummary'
import type { ProBoardCounts } from './domain/ProBoardCounts'

export function useProReservations() {
  const { isAuthenticated } = useKeycloak()
  const reservations = ref<ProReservationSummary[]>([])
  const boardCounts = ref<ProBoardCounts>({ countNouvelle: 0, countEnDiscussion: 0, countConfirmee: 0 })
  const loading = ref(true)
  const error = ref<unknown>(null)

  async function load() {
    loading.value = true
    try {
      ;[reservations.value, boardCounts.value] = await Promise.all([
        fetchProReservations(),
        fetchProBoardCounts(),
      ])
    } catch (e) {
      error.value = e
    } finally {
      loading.value = false
    }
  }

  watch(isAuthenticated, (authenticated) => {
    if (authenticated) load()
  }, { immediate: true })

  return { reservations, boardCounts, loading, error }
}
