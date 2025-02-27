import type { EventActivityType } from '@/types/EventActivityType'

export interface EventActivity {
  id: string
  date: Date
  partnerName: string
  partnerAvatarUrl: string
  eventActivityType: EventActivityType
  payload?: Record<string, unknown>
}
