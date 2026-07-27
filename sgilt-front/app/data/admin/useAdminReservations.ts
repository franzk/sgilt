/**
 * Composable — état de la liste admin des réservations (back-office)
 */
import { fetchAdminReservations } from './service/adminService'
import type { AdminReservationListItem } from './domain/AdminReservationListItem'
import type { AdminReservationStatus } from './domain/AdminReservationStatus'

const rows = ref<AdminReservationListItem[]>([])
const loading = ref(false)
const statusFilter = ref<AdminReservationStatus | null>(null)

export function useAdminReservations() {
  async function load() {
    loading.value = true
    try {
      rows.value = await fetchAdminReservations(statusFilter.value ?? undefined)
    } finally {
      loading.value = false
    }
  }

  async function setStatusFilter(status: AdminReservationStatus | null) {
    statusFilter.value = status
    await load()
  }

  return {
    rows,
    loading,
    statusFilter,
    load,
    setStatusFilter,
  }
}
