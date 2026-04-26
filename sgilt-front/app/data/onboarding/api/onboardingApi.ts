import { apiFetch } from '~/composables/useApi'
import type { DemandeRequest } from '~/types/demande'
import type {
  ConfirmAccountRequestDto,
  ConfirmAccountResponseDto,
  VerifyTokenResponseDto,
} from '../dto/OnboardingDto'

export async function submitOnboarding(body: DemandeRequest): Promise<void> {
  await apiFetch('/onboarding', { method: 'POST', body })
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
