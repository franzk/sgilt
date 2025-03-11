<template>
  <div class="event-board">
    <!-- Event overview section -->
    <section class="event" v-if="activeMobileView === 'event' || !isMobileView">
      <!-- Event summary for mobile view -->
      <EventSummary :sgiltEvent="sgiltEvent" class="event-summary" />

      <!-- EventTracker component -->
      <EventTracker :event="sgiltEvent" />
      <div class="help-panel-toggler" @click="helpPanelVisible = !helpPanelVisible">
        {{ $t('texts.need-help') }}
      </div>
    </section>

    <!-- Event components section -->
    <section class="event-components">
      <!-- Help Panel  -->
      <aside
        v-if="activeMobileView === 'help' || !isMobileView"
        class="help-panel"
        :class="{ open: helpPanelVisible }"
      >
        <span class="help-panel-close" @click="helpPanelVisible = false"
          ><SgiltIcon icon="Close"
        /></span>

        <!-- Event summary for desktop & tablet views -->
        <EventSummary :sgiltEvent="sgiltEvent" class="event-summary" />

        <!-- EventHelpPanel component -->
        <EventHelpPanel @close="helpPanelVisible = false" />
      </aside>

      <!-- ReservationsBoard component -->
      <div v-if="activeMobileView === 'reservations' || !isMobileView" class="reservations-board">
        <ReservationsBoard :reservations="sgiltEvent?.reservations">
          <template #firstCell v-if="isTabletView">
            <EventSummary :sgiltEvent="sgiltEvent" />
          </template>
        </ReservationsBoard>
      </div>

      <!-- EventActivityFeed component -->
      <aside class="event-activity-feed" v-if="activeMobileView === 'activities' || !isMobileView">
        <EventActivityFeed :activities="activities" />
      </aside>
    </section>
  </div>

  <MobileBottomNavBar :activeView="activeMobileView" @update-view="updateMobileView($event)" />
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
import EventSummary from '@/components/event_board/EventSummary.vue'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'
import { useResponsiveView } from '@/composable/useResponsiveView'

const route = useRoute()
const sgiltEvent = ref<SgiltEvent>()

const activities = ref<EventActivity[]>([])

onMounted(async () => {
  const id = parseInt(Array.isArray(route.params.id) ? route.params.id[0] : route.params.id)
  sgiltEvent.value = await getTestEvent(id)

  activities.value = findAllEventActivities()
})

const { isMobileView, isTabletView } = useResponsiveView()

const helpPanelVisible = ref(!isTabletView)

// responsive mobile view
const activeMobileView = ref('event')
const updateMobileView = (view: string) => {
  activeMobileView.value = view
}
</script>

<style scoped lang="scss">
$aside-width: 20rem;

.event-board {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.event {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding-top: $spacing-m;
  position: relative;
  align-items: center;

  .event-summary {
    @include respond-to(not-mobile) {
      display: none;
    }
  }
}

.event-components {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;

  // left panel : help panel
  .help-panel {
    flex: 0 0 0; // it begins with no width
    display: flex;
    flex-direction: column;
    position: relative;
    gap: $spacing-m;
    overflow: hidden;
    opacity: 0;

    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.2);

    @include respond-to(mobile) {
      flex: 1;
      opacity: 1;
      gap: 0;
      padding-bottom: $spacing-xxxl;
    }

    transition:
      opacity 0.4s ease-in-out,
      flex 0.2s ease-in-out;

    &.open {
      @include respond-to(not-mobile) {
        flex: 0 0 $aside-width; // it takes the width
        opacity: 1;
      }
    }

    .event-summary {
      @include respond-to(tablet) {
        display: none;
      }
    }

    .help-panel-close {
      position: absolute;
      z-index: $z-first-floor;
      top: $spacing-s;
      right: $spacing-s;
      cursor: pointer;
      @include respond-to(mobile) {
        display: none;
      }
    }
  }

  // reservations board in the middle
  .reservations-board {
    flex: 1;
  }

  // right panel activity feed
  .event-activity-feed {
    width: $aside-width;
    @include respond-to(mobile) {
      width: unset;
    }
  }
}

.help-panel-toggler {
  @include respond-to(mobile) {
    display: none;
  }

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
