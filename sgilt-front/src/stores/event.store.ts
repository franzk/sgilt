import type { SgiltEvent } from '@/data/domain/SgiltEvent'
import { getTestEvent } from '@/data/repository/TestEventRepository'
import type { ReservationStatusKey } from '@/types/ReservationStatus'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { findAllEventActivities } from '@/data/repository/EventActivityRepository'

export const useEventStore = defineStore('event', () => {
  const sgiltEvent = ref<SgiltEvent>()

  const loadEvent = async (id: string) => {
    sgiltEvent.value = await getTestEvent(id)
  }

  // Reservations zone
  const reservationsCount = computed(() => sgiltEvent.value?.reservations.length || 0)

  const reservationsCountByStatus = (status: ReservationStatusKey): number =>
    sgiltEvent.value?.reservations.filter((r) => r.status === status).length || 0

  // Activities zone
  const activities = computed(() => findAllEventActivities())

  return { sgiltEvent, loadEvent, reservationsCount, reservationsCountByStatus, activities }
})
