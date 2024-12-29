import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Category } from '@/domain/Category'

/**
 *
 */
export const useCategorysStore = defineStore('categories', () => {
  // reactive state
  const categories = ref<Category[]>([
    { name: 'music', imageUrl: './images/music.jpg' },
    { name: 'food', imageUrl: './images/food.jpg' },
    { name: 'place', imageUrl: './images/place.jpg' },
    { name: 'photo', imageUrl: './images/photo.jpg' },
  ])

  return { categories }
})
