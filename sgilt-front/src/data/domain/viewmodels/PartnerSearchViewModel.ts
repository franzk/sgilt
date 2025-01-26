import type { Tag } from '@/data/domain/Tag'

export interface PartnerSearchViewModel {
  id: string
  title: string
  slug: string
  description: string
  imageUrl: string
  tags: Tag[]
  entryPrice: number
  availability?: string
}
