<template>
  <div class="event-board">
    <!-- EventTracker component -->
    <section class="event-tracker">
      <EventTracker :event="sgiltEvent" />
      <div class="help-panel-toggler" @click="helpPanelVisible = !helpPanelVisible">
        {{ $t('texts.need-help') }}
      </div>
    </section>

    <!-- Event components section -->
    <section class="event-components">
      <!-- Help Panel component -->
      <aside class="help-panel" :class="{ open: helpPanelVisible }">
        <EventHelpPanel @close="helpPanelVisible = false" />
      </aside>
      <!-- ReservationsBoard component -->
      <div class="reservations-board">
        <ReservationsBoard :reservations="sgiltEvent?.reservations" />
      </div>
      <!-- EventActivityFeed component -->
      <aside class="event-activity-feed">
        <EventActivityFeed :activities="activities" />
      </aside>
    </section>
  </div>

  <MobileBottomNavBar activeView="event" />
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
import EventHelpPanel from '@/components/event_board/help_panel/EventHelpPanel.vue'
import MobileBottomNavBar from '@/components/event_board/MobileBottomNavBar.vue'

const route = useRoute()
const sgiltEvent = ref<SgiltEvent>()

const activities = ref<EventActivity[]>([])

onMounted(async () => {
  const id = parseInt(Array.isArray(route.params.id) ? route.params.id[0] : route.params.id)
  sgiltEvent.value = await getTestEvent(id)

  activities.value = findAllEventActivities()
})

const helpPanelVisible = ref(false)
</script>

<style scoped lang="scss">
$aside-width: 20rem;

.event-board {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.event-tracker {
  position: relative;
}

.event-components {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;

  // left panel : help panel
  .help-panel {
    flex: 0 0 0; // it begins with no width
    display: flex;
    overflow: hidden;
    opacity: 0;

    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.2);

    transition:
      opacity 0.4s ease-in-out,
      flex 0.2s ease-in-out;

    &.open {
      flex: 0 0 $aside-width; // it takes the width
      opacity: 1;
    }
  }

  // reservations board in the middle
  .reservations-board {
    flex: 1;
  }

  // right panel activity feed
  .event-activity-feed {
    width: $aside-width;
  }
}

.help-panel-toggler {
  position: absolute;
  bottom: 0;
  left: $spacing-s;

  cursor: pointer;
  font-style: italic;

  &:hover {
    text-decoration: underline;
    font-weight: 600;
    transition: font-weight 0.3s ease-in-out text-decoration 0.3s ease-in-out;
  }
}
</style>
