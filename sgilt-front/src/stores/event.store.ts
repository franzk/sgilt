import type { SgiltEvent } from '@/data/domain/SgiltEvent'
import { getTestEvent } from '@/data/repository/TestEventRepository'
import type { ReservationStatusKey } from '@/types/ReservationStatus'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export const useEventStore = defineStore('event', () => {
  const sgiltEvent = ref<SgiltEvent>()

  const loadEvent = async (id: string) => {
    sgiltEvent.value = await getTestEvent(id)
  }

  const reservationsCount = computed(() => sgiltEvent.value?.reservations.length || 0)

  const reservationsCountByStatus = (status: ReservationStatusKey): number =>
    sgiltEvent.value?.reservations.filter((r) => r.status === status).length || 0

  return { sgiltEvent, loadEvent, reservationsCount, reservationsCountByStatus }
})
