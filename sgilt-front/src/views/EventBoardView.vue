<template>
  <div class="event-board">
    <section class="event-tracker">
      <EventTracker :event="sgiltEvent" />
    </section>
  </div>
</template>

<script setup lang="ts">
import type { SgiltEvent } from '@/data/domain/SgiltEvent'
import { getTestEvent } from '@/data/repository/EventRepository'
import { onMounted, ref } from 'vue'
import EventTracker from '@/components/event_board/EventTracker.vue'

const sgiltEvent = ref<SgiltEvent>()

onMounted(async () => {
  sgiltEvent.value = await getTestEvent()
})
</script>

<style scoped lang="scss">
.event-board {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: $spacing-m 0;
  background: $color-secondary;
}
</style>
