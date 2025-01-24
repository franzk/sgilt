import { defineStore } from 'pinia'
import { getPartnerBySlug } from '@/data/services/PartnerService'
import type { Partner } from '@/data/domain/Partner'
import { ref } from 'vue'

export const usePartnerStore = defineStore('partner', () => {
  const partner = ref<Partner>()

  const fetchPartner = async (slug: string): Promise<void> => {
    partner.value = await getPartnerBySlug(slug)
    return new Promise((resolve, reject) => {
      if (partner.value) {
        resolve()
      } else {
        reject()
      }
    })
  }

  return { partner, fetchPartner }
})
