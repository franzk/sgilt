<template>
  <div class="event-board">
    <!-- EventTracker component -->
    <section class="event-tracker">
      <EventTracker :event="sgiltEvent" />
      <div class="floating-button-container" @click="helpPanelVisible = !helpPanelVisible">
        Besoin d'aide ?
      </div>
    </section>
    <section class="event-components">
      <!-- Help Panel component -->
      <div class="help-panel" :class="{ open: helpPanelVisible }">
        <EventHelpPanel @close="helpPanelVisible = false" />
      </div>
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
import EventHelpPanel from '@/components/event_board/help_panel/EventHelpPanel.vue'

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
$activity-feed-width: 20rem;

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

  /* PANEL D'AIDE */
  .help-panel {
    flex: 0 0 0; /* Il commence avec une largeur de 0 */
    display: flex;
    overflow: hidden;
    opacity: 0;
    transform: translateX(-100%); /* Déplacé complètement à gauche */

    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.2);

    transition:
      transform 0.4s ease-out,
      opacity 0.3s ease-in-out,
      flex 0.4s ease-out;
  }

  /* Quand il s'affiche */
  .help-panel.open {
    flex: 0 0 $activity-feed-width; /* Il prend sa place */
    transform: translateX(0); /* Il revient en position normale */
    opacity: 1;
  }

  .reservations-board {
    flex: 1;
  }
  .event-activity-feed {
    width: $activity-feed-width;
  }
}

.floating-button-container {
  display: flex;
  justify-content: flex-start;
  margin-left: $spacing-s;
  position: absolute;
  bottom: 0;
  left: 0;
  cursor: pointer;
  font-style: italic;

  &:hover {
    text-decoration: underline;
  }
}
</style>
