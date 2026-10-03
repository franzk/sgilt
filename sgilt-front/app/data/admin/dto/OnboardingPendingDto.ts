export type OnboardingPendingState = 'OPEN' | 'PENDING_CONFIRMATION'

export interface OnboardingPendingDto {
  id: string
  email: string
  eventType: string
  eventDate: string | null
  demandeCount: number
  state: OnboardingPendingState
  createdAt: string
  expiresAt: string
}
