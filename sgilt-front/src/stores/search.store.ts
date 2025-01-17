import type { Partner } from '@/data/domain/Partner'
import { searchPartners } from '@/data/services/PartnerService'
import type { PartnerQuery } from '@/data/api/query/PartnerQuery'
import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * Search store
 * contains search results and search action
 */
export const useSearchStore = defineStore('search', () => {
  // reactive state
  const results = ref<Partner[]>([])
  const filtersCount = ref(0)

  // actions
  const search = async (query: PartnerQuery) => {
    // store the current query filters count
    filtersCount.value =
      Object.values(query).filter((v) => v && !Array.isArray(v)).length +
      (query.tagsId?.length || 0)

    // fetch partners from the API
    results.value = await searchPartners(query)
  }

  return { results, filtersCount, search }
})
