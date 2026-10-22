import type { OnboardingPendingState } from '../dto/OnboardingPendingDto'

export interface OnboardingPending {
  id: string
  email: string
  eventType: string
  eventDate: string | null
  demandeCount: number
  state: OnboardingPendingState
  createdAt: string
  expiresAt: string
}
