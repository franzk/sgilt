import type { Tag } from '@/data/domain/Tag'

export interface Partner {
  id: string
  title: string
  slug: string
  description: string
  longDescription: string
  imageUrl: string
  tags: Tag[]
  entryPrice: number
  prices?: Price[]
}

export interface Price {
  id: string
  title: string
  price: number
}
