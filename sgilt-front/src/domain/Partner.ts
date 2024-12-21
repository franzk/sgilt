import type { Category } from '@/domain/Category'

export interface Partner {
  id: string
  title: string
  description: string
  imageUrl: string
  category: Category
  entryPrice: number
}
