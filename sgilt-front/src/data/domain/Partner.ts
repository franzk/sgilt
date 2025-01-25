import type { Tag } from '@/data/domain/Tag'

export interface Partner {
  id: string
  title: string
  slug: string
  description: string
  longDescription: string
  imageUrl: string
  youtubeId?: string
  tags: Tag[]
  entryPrice: number
  prices?: Price[]
  calendar?: CalendarEntry[]
}

export interface Price {
  id: string
  title: string
  description?: string
  price: number
  unity?: string
  minQuantity?: number
}

export interface CalendarEntry {
  id: string
  date: Date
  state: 'booked' | 'option'
}
