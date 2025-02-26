<template>
  <div class="event-board">
    <section class="event-tracker">
      <EventTracker :event="sgiltEvent" />
    </section>
    <div>
      <ReservationsBoard :reservations="sgiltEvent?.reservations" />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { SgiltEvent } from '@/data/domain/SgiltEvent'
import { getTestEvent } from '@/data/repository/EventRepository'
import { onMounted, ref } from 'vue'
import EventTracker from '@/components/event_board/EventTracker.vue'
import ReservationsBoard from '@/components/event_board/ReservationsBoard.vue'

const sgiltEvent = ref<SgiltEvent>()

onMounted(async () => {
  sgiltEvent.value = await getTestEvent()
})
</script>

<style scoped lang="scss">
.event-board {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
</style>
