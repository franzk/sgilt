export type OnboardingPendingState = 'OPEN' | 'PENDING_CONFIRMATION'

export interface OnboardingPendingDto {
  id: string
  email: string
  prestataireName: string
  state: OnboardingPendingState
  createdAt: string
  expiresAt: string
}
