import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Tag } from '@/data/domain/Tag'
import { getAllTags } from '@/data/services/TagService'

/**
 * Tags store with mock data
 */
export const useTagsStore = defineStore('tags', () => {
  // reactive state
  const tags = ref<Tag[]>(getAllTags())

  return { tags }
})
