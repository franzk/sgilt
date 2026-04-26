import type { DemandeRequest } from '~/types/demande'

/**
 * Soumet le formulaire de demande d'onboarding.
 */
export async function submitOnboarding(body: DemandeRequest): Promise<void> {
  await apiFetch('/onboarding', { method: 'POST', body })
}
