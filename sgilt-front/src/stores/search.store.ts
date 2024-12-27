import type { Partner } from '@/domain/Partner'
import { PartnerService } from '@/services/PartnerService'
import type { PartnerQuery } from '@/types/PartnerQuery'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSearchStore = defineStore('search', () => {
  // reactive state
  const results = ref<Partner[]>([])

  // actions
  const search = async (query: PartnerQuery) => {
    // fetch partners from the API
    results.value = await PartnerService.search(query)
  }

  return { results, search }
})
