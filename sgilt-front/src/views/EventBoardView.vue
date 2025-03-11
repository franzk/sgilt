<template>
  <div class="event-board">
    <!-- Event overview section -->
    <section class="event" v-if="activeMobileView === 'event' || !isMobileView">
      <!-- Event summary for mobile view -->
      <EventSummary v-if="isMobileView" :sgiltEvent="sgiltEvent" showEventDetails />

      <!-- EventTracker component -->
      <EventTracker :event="sgiltEvent" :showFinalStep="!isTabletView" />

      <!-- Help Panel Toggler -->
      <div
        v-if="!isMobileView"
        class="help-panel-toggler"
        @click="helpPanelVisible = !helpPanelVisible"
      >
        {{ $t('texts.need-help') }}
      </div>
    </section>

    <!-- Event components section -->
    <section class="event-components">
      <!-- Help Panel  -->
      <aside
        v-if="activeMobileView === 'help' || !isMobileView"
        :class="['help-panel', { open: helpPanelVisible }]"
      >
        <span class="help-panel-close" @click="helpPanelVisible = false"
          ><SgiltIcon icon="Close"
        /></span>

        <!-- Event summary for desktop view -->
        <EventSummary v-if="isDesktopView" :sgiltEvent="sgiltEvent" />

        <!-- EventHelpPanel component -->
        <EventHelpPanel />
      </aside>

      <!-- ReservationsBoard component -->
      <div v-if="activeMobileView === 'reservations' || !isMobileView" class="reservations-board">
        <ReservationsBoard :reservations="sgiltEvent?.reservations">
          <!-- First cell is the EventSummary component in tablet view -->
          <template #firstCell v-if="isTabletView">
            <EventSummary :sgiltEvent="sgiltEvent" showEventDetails />
          </template>
        </ReservationsBoard>
      </div>

      <!-- EventActivityFeed component -->
      <aside class="event-activity-feed" v-if="activeMobileView === 'activities' || !isMobileView">
        <EventActivityFeed :activities="activities" />
      </aside>
    </section>
  </div>

  <!-- Mobile bottom navigation bar -->
  <MobileBottomNavBar :activeView="activeMobileView" @update-view="updateMobileView($event)" />
</template>

<script setup lang="ts">
import type { SgiltEvent } from '@/data/domain/SgiltEvent'
import { computed, onMounted, ref } from 'vue'
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

const sgiltEvent = ref<SgiltEvent>()
const activities = ref<EventActivity[]>([])

// fetch event data
const route = useRoute()
onMounted(async () => {
  const id = parseInt(Array.isArray(route.params.id) ? route.params.id[0] : route.params.id)
  sgiltEvent.value = await getTestEvent(id)

  activities.value = findAllEventActivities()
})

// responsive view
const { isMobileView, isTabletView } = useResponsiveView()
const isDesktopView = computed(() => !isMobileView.value && !isTabletView.value)

// help panel visibility
const helpPanelVisible = ref()
onMounted(() => (helpPanelVisible.value = isDesktopView.value))

// active view for mobile
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

  @include respond-to(mobile) {
    // space for the bottom navigation bar
    margin-bottom: $spacing-xxxl;
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

    &.open {
      @include respond-to(not-mobile) {
        flex: 0 0 $aside-width; // it takes the width
        opacity: 1;
      }
    }

    @include respond-to(mobile) {
      flex: 1;
      opacity: 1;
      gap: 0;
      padding-bottom: $spacing-xxxl;
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
