import type { Partner } from '@/domain/Partner'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSearchStore = defineStore('search', () => {
  // reactive state
  const partners = ref<Partner[]>([])

  return { partners }
})
