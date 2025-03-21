export type EventActivityType =
  | 'reservation-sent'
  | 'reservation-viewed'
  | 'message-sent'
  | 'message-received'
  | 'reservation-approved'
  | 'payment-request'
  | 'payment-success'
  | 'payment-failed'
  | 'reservation-cancelled-user'
  | 'reservation-cancelled-partner'
  | 'reservation-updated'
  | 'reservation-modified-user'
  | 'partner-unavailable'

export interface EventActivity {
  id: string
  date: Date
  partnerName: string
  partnerAvatarUrl: string
  eventActivityTypeId: EventActivityType
  payload?: Record<string, unknown>
}
