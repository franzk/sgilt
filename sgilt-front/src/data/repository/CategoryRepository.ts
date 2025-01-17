import type { Category } from '@/data/domain/Category'

/**
 * Get all categories
 * @returns {Category[]} categories
 */
export const findAllCategories = (): Category[] => {
  return categories
}

// Mock data
const categories: Category[] = [
  { name: 'music', imageUrl: './images/music.jpg' },
  { name: 'food', imageUrl: './images/food.jpg' },
  { name: 'place', imageUrl: './images/place.jpg' },
  { name: 'photo', imageUrl: './images/photo.jpg' },
]
