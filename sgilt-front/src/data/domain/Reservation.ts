import type { Message } from '@/data/domain/Message'
import type { Partner, Price } from '@/data/domain/Partner'
import type { ReservationStatusKey } from '@/types/ReservationStatus'

export interface Reservation {
  id: string
  partner: Partner
  price: Price
  quantity?: number
  totalPrice: number
  messages: Message[]
  status: ReservationStatusKey
  createdAt: Date
}
