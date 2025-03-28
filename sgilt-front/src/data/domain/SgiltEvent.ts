import type { Reservation } from '@/data/domain/Reservation'

export interface SgiltEvent {
  title: string
  description: string
  eventType: string
  location: string
  dateTime: Date
  reservations: Reservation[]
}
