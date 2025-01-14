import { findAllCategories } from '@/data/repository/CategoryRepository'
import type { Category } from '@/data/domain/Category'

/**
 * Get all categories
 * @returns {Category[]} categories
 */
export const getAllCategories = (): Category[] => {
  return findAllCategories()
}
