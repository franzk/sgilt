<template>
  <template v-if="isMobileView"><EventBoardMobile :sgiltEvent="sgiltEvent" /></template>
  <div v-else class="event-board">
    <!-- Event overview section -->
    <section
      :class="['event', { 'mobile-layout': isMobileView }]"
      v-if="activeMobileView === 'event' || !isMobileView"
    >
      <!-- Event summary for mobile view -->
      <EventSummary v-if="isMobileView" :sgiltEvent="sgiltEvent" showEventDetails />

      <!-- EventTracker component -->
      <EventTracker
        :event="sgiltEvent"
        :orientation="isMobileView ? 'vertical' : 'horizontal'"
        :showFinalStep="!isTabletView"
        class="event-tracker"
      />

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
    <section :class="['event-components', { 'mobile-layout': isMobileView }]">
      <!-- Help Panel  -->
      <aside
        v-if="activeMobileView === 'help' || !isMobileView"
        :class="['help-panel', { open: helpPanelVisible, 'mobile-layout': isMobileView }]"
      >
        <span v-if="!isMobileView" class="help-panel-close" @click="helpPanelVisible = false"
          ><SgiltIcon icon="Close"
        /></span>

        <MobileScreenTitle v-if="isMobileView" icon="Help" title="Aide" />

        <!-- Event summary for desktop view -->
        <EventSummary v-if="isDesktopView" :sgiltEvent="sgiltEvent" class="desktop-event-summary" />

        <!-- EventHelpPanel component -->
        <EventHelpPanel />
      </aside>

      <!-- ReservationsBoard component -->
      <div
        v-if="activeMobileView === 'reservations' || !isMobileView"
        :class="['reservations-board', { 'mobile-layout': isMobileView }]"
      >
        <MobileScreenTitle v-if="isMobileView" icon="List" title="Réservations" />

        <ReservationsBoard :reservations="sgiltEvent?.reservations">
          <!-- First cell is the EventSummary component in tablet view -->
          <template #firstCell v-if="isTabletView">
            <EventSummary :sgiltEvent="sgiltEvent" showEventDetails />
          </template>
        </ReservationsBoard>
      </div>

      <!-- EventActivityFeed component -->
      <aside
        v-if="activeMobileView === 'activities' || !isMobileView"
        :class="['event-activity-feed', { 'mobile-layout': isMobileView }]"
      >
        <MobileScreenTitle v-if="isMobileView" icon="Bell" title="Activité" />

        <EventActivityFeed :activities="activities" :isMobileView="isMobileView" />
      </aside>
    </section>
  </div>

  <!-- Mobile bottom navigation bar -->
  <!--MobileBottomNavBar
    v-if="isMobileView"
    :activeView="activeMobileView"
    @update-view="updateMobileView($event)"
  /-->
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
// import MobileBottomNavBar from '@/components/event_board/MobileBottomNavBar.vue'
import EventSummary from '@/components/event_board/EventSummary.vue'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'
import { useResponsiveView } from '@/composable/useResponsiveView'
import EventBoardMobile from '@/components/event_board/mobile/EventBoardMobile.vue'

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
const { isMobileView, isTabletView } = useResponsiveView(890)
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

  gap: $spacing-m;
  padding-top: $spacing-m;
  position: relative;
  align-items: center;

  &.mobile-layout {
    flex-direction: column;
    // space for the bottom navigation bar
    margin-bottom: $spacing-xxxl;
  }

  .event-tracker {
    flex: 1;
  }
}

.event-components {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;

  &.mobile-layout {
    justify-content: center;
  }

  // left panel : help panel
  .help-panel {
    flex: 0 0 0; // it begins with no width
    height: 100%;
    display: flex;
    flex-direction: column;
    position: relative;
    gap: $spacing-m;
    padding: $spacing-s;
    overflow: hidden;
    opacity: 0;
    box-shadow: $box-shadow;

    &.open {
      flex: 0 0 $aside-width; // it takes the width
      opacity: 1;
    }

    // fade in effect
    transition:
      opacity 0.4s ease-in-out,
      flex 0.2s ease-in-out;

    &.mobile-layout {
      flex: 1;
      opacity: 1;
      gap: 0;
      padding: $spacing-l 0 $spacing-xxxl 0;
    }

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
