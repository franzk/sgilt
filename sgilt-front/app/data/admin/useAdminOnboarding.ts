/**
 * Composable — état des onboardings prestataire en attente (back-office)
 */
import { fetchPendingOnboardings, resendOnboardingEmail } from './service/adminService'
import type { PrestataireOnboardingPending } from './domain/PrestataireOnboardingPending'

const rows = ref<PrestataireOnboardingPending[]>([])
const loading = ref(false)
const resendingId = ref<string | null>(null)
const resendErrorId = ref<string | null>(null)
const resentId = ref<string | null>(null)

export function useAdminOnboarding() {
  async function load() {
    loading.value = true
    try {
      rows.value = await fetchPendingOnboardings()
    } finally {
      loading.value = false
    }
  }

  async function resend(prestataireId: string): Promise<void> {
    resendingId.value = prestataireId
    resendErrorId.value = null
    resentId.value = null
    try {
      await resendOnboardingEmail(prestataireId)
      resentId.value = prestataireId
      await load()
    } catch {
      resendErrorId.value = prestataireId
    } finally {
      resendingId.value = null
    }
  }

  return {
    rows,
    loading,
    load,
    resend,
    resendingId,
    resendErrorId,
    resentId,
  }
}
