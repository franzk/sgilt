import type { SgiltEvent } from '@/data/domain/SgiltEvent'
import { getTestEvent } from '@/data/repository/TestEventRepository'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useEventStore = defineStore('event', () => {
  const sgiltEvent = ref<SgiltEvent>()

  const loadEvent = async (id: string) => {
    sgiltEvent.value = await getTestEvent(id)
  }

  return { sgiltEvent, loadEvent }
})
