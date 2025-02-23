import type { Message } from '@/data/domain/Message'
import type { Partner, Price } from '@/data/domain/Partner'

export interface Reservation {
  id: string
  partner: Partner
  price: Price
  quantity?: number
  totalPrice: number
  messages: Message[]
  status: 'pending' | 'viewed' | 'approved' | 'declined' | 'canceled' | 'paied' | 'completed'
  createdAt: Date
}
