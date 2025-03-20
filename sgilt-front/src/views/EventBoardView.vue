<template>
  <template v-if="isMobileView"><EventBoardMobile /></template>
  <div v-else class="event-board">
    <!-- Event overview section -->
    <section class="event">
      <!-- EventTracker component -->
      <EventTracker :showFinalStep="!isTabletView" class="event-tracker" />

      <!-- Help Panel Toggler -->
      <div class="help-panel-toggler" @click="helpPanelVisible = !helpPanelVisible">
        {{ $t('texts.need-help') }}
      </div>
    </section>

    <!-- Event components section -->
    <section class="event-components">
      <!-- Help Panel  -->
      <aside :class="['help-panel', { open: helpPanelVisible }]">
        <span class="help-panel-close" @click="helpPanelVisible = false">
          <SgiltIcon icon="Close" />
        </span>

        <!-- Event summary for desktop view -->
        <EventSummary v-if="isDesktopView" class="desktop-event-summary" />

        <!-- EventHelpPanel component -->
        <EventHelpPanel />
      </aside>

      <!-- ReservationsBoard component -->
      <div class="reservations-board">
        <ReservationsBoard :reservations="eventStore.sgiltEvent?.reservations">
          <!-- First cell is the EventSummary component in tablet view -->
          <template #firstCell v-if="isTabletView">
            <EventSummary showEventDetails />
          </template>
        </ReservationsBoard>
      </div>

      <!-- EventActivityFeed component -->
      <aside class="event-activity-feed">
        <EventActivityFeed :activities="activities" />
      </aside>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import EventTracker from '@/components/event_board/EventTracker.vue'
import ReservationsBoard from '@/components/event_board/ReservationsBoard.vue'
import { findAllEventActivities } from '@/data/repository/EventActivityRepository'
import type { EventActivity } from '@/data/domain/EventActivity'
import EventActivityFeed from '@/components/event_board/activity_feed/EventActivityFeed.vue'
import { useRoute } from 'vue-router'
import EventHelpPanel from '@/components/event_board/help_panel/EventHelpPanel.vue'
import EventSummary from '@/components/event_board/EventSummary.vue'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'
import { useResponsiveView } from '@/composable/useResponsiveView'
import EventBoardMobile from '@/components/event_board/mobile/EventBoardMobile.vue'
import { useEventStore } from '@/stores/event.store'

const activities = ref<EventActivity[]>([])

const eventStore = useEventStore()

// fetch event data
const route = useRoute()
onMounted(async () => {
  const id = Array.isArray(route.params.id) ? route.params.id[0] : route.params.id
  eventStore.loadEvent(id)
  activities.value = findAllEventActivities()
})

// responsive view
const { isMobileView, isTabletView } = useResponsiveView(890)
const isDesktopView = computed(() => !isMobileView.value && !isTabletView.value)

// help panel visibility
const helpPanelVisible = ref()
onMounted(() => (helpPanelVisible.value = isDesktopView.value))
</script>

<style scoped lang="scss">
$aside-width: 20rem;

.event-board {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.event {
  display: flex;

  gap: $spacing-m;
  padding-top: $spacing-m;
  position: relative;
  align-items: center;

  .event-tracker {
    flex: 1;
  }
}

.event-components {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;

  // left panel : help panel
  .help-panel {
    flex: 0 0 0; // it begins with no width
    // height: 100%;
    display: flex;
    flex-direction: column;
    position: relative;
    gap: $spacing-m;
    padding: $spacing-s;
    overflow: hidden;
    opacity: 0;
    // box-shadow: $box-shadow;

    &.open {
      flex: 0 0 $aside-width; // it takes the width
      opacity: 1;
    }

    // fade in effect
    transition:
      opacity 0.4s ease-in-out,
      flex 0.2s ease-in-out;

    // close button
    .help-panel-close {
      position: absolute;
      z-index: $z-first-floor;
      top: $spacing-s;
      right: $spacing-s;
      cursor: pointer;
    }

    .desktop-event-summary {
      box-shadow: $box-shadow;
    }
  }

  // reservations board in the middle
  .reservations-board {
    flex: 1;
    &.mobile-layout {
      padding: $spacing-l $spacing-l $spacing-xxxl $spacing-l;
    }
  }

  // right panel activity feed
  .event-activity-feed {
    width: $aside-width;
    &.mobile-layout {
      padding: $spacing-l 0 $spacing-xxxl 0;
      width: unset;
    }
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
