export interface EventActivity {
  id: string
  date: Date
  partnerName: string
  partnerAvatarUrl: string
  eventActivityTypeId: string
  payload?: Record<string, unknown>
}
