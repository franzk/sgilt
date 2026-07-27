/**
 * Composable — état des sessions d'onboarding utilisateur (client) en attente (back-office)
 */
import { fetchPendingUserOnboardings } from './service/adminService'
import type { OnboardingPending } from './domain/OnboardingPending'

const rows = ref<OnboardingPending[]>([])
const loading = ref(false)

export function useAdminUserOnboarding() {
  async function load() {
    loading.value = true
    try {
      rows.value = await fetchPendingUserOnboardings()
    } finally {
      loading.value = false
    }
  }

  return {
    rows,
    loading,
    load,
  }
}
