import type { Tag } from '@/domain/Tag'

export interface Partner {
  id: string
  title: string
  description: string
  imageUrl: string
  tags: Tag[]
  entryPrice: number
}
