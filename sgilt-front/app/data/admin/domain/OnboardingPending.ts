import type { OnboardingPendingState } from '../dto/OnboardingPendingDto'

export interface OnboardingPending {
  id: string
  email: string
  prestataireName: string
  state: OnboardingPendingState
  createdAt: string
  expiresAt: string
}
