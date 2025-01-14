import { findAllTags } from '@/data/repository/TagRepository'
import type { Tag } from '@/data/domain/Tag'

/**
 * Get all tags
 * @returns {Tag[]} tags
 */
export const getAllTags = (): Tag[] => {
  return findAllTags()
}
