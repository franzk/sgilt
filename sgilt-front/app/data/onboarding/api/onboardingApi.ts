import { apiFetch } from '~/composables/useApi'
import type { DemandeRequest } from '~/types/demande'

export async function submitOnboarding(body: DemandeRequest): Promise<void> {
  await apiFetch('/onboarding', { method: 'POST', body })
}
