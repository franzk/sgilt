import { apiFetch } from '~/composables/useApi'
import type { OnboardingDemandeRequest } from '~/types/demande'
import type {
  ConfirmAccountRequestDto,
  ConfirmAccountResponseDto,
  VerifyTokenResponseDto,
} from '../dto/OnboardingDto'

// Onboarding par demande unique (fiche d'un prestataire) : le back construit l'événement complet.
export async function submitOnboarding(body: OnboardingDemandeRequest): Promise<void> {
  await apiFetch('/onboarding/demande', { method: 'POST', body })
}

export async function verifyOnboardingToken(token: string): Promise<VerifyTokenResponseDto> {
  return apiFetch<VerifyTokenResponseDto>('/onboarding/verify', { query: { token } })
}

export async function confirmOnboardingAccount(
  body: ConfirmAccountRequestDto,
): Promise<ConfirmAccountResponseDto> {
  return apiFetch<ConfirmAccountResponseDto>('/onboarding/confirm-account', {
    method: 'POST',
    body,
  })
}
