<template>
  <div class="event-board">
    <!-- EventTracker component -->
    <section class="event-tracker">
      <EventTracker :event="sgiltEvent" />
    </section>
    <section class="event-components">
      <!-- ReservationsBoard component -->
      <div class="reservations-board">
        <ReservationsBoard :reservations="sgiltEvent?.reservations" />
      </div>
      <!-- EventActivityFeed component -->
      <div class="event-activity-feed">
        <EventActivityFeed :activities="activities" />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { SgiltEvent } from '@/data/domain/SgiltEvent'
import { onMounted, ref } from 'vue'
import EventTracker from '@/components/event_board/EventTracker.vue'
import ReservationsBoard from '@/components/event_board/ReservationsBoard.vue'
import { findAllEventActivities } from '@/data/repository/EventActivityRepository'
import type { EventActivity } from '@/data/domain/EventActivity'
import EventActivityFeed from '@/components/event_board/activity_feed/EventActivityFeed.vue'
import { getTestEvent } from '@/data/repository/TestEventRepository'
import { useRoute } from 'vue-router'

const route = useRoute()
const sgiltEvent = ref<SgiltEvent>()

const activities = ref<EventActivity[]>([])

onMounted(async () => {
  const id = parseInt(Array.isArray(route.params.id) ? route.params.id[0] : route.params.id)
  sgiltEvent.value = await getTestEvent(id)

  activities.value = findAllEventActivities()
})
</script>

<style scoped lang="scss">
$activity-feed-width: 20rem;

.event-board {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.event-components {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;
  .reservations-board {
    flex: 1;
  }
  .event-activity-feed {
    width: $activity-feed-width;
  }
}
</style>
