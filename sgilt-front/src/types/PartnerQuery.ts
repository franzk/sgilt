import type { CategoryTagFilter } from '@/types/CategoryFilter'

export interface PartnerQuery {
  dateFilter?: Date
  minPrice?: number
  maxPrice?: number
  categroryTags?: CategoryTagFilter[]
}
