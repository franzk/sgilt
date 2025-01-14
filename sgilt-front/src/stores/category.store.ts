import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Category } from '@/data/domain/Category'
import { getAllCategories } from '@/data/services/CategoryService'

/**
 *
 */
export const useCategorysStore = defineStore('categories', () => {
  // reactive state
  const categories = ref<Category[]>(getAllCategories())

  return { categories }
})
