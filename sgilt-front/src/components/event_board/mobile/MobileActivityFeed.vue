<template>
  <div class="event-activity-feed-mobile">
    <MobileScreenTitle icon="Bell" label="Notifications" />
    <EventActivityFeed mobile />
  </div>
</template>

<script setup lang="ts">
import EventActivityFeed from '@/components/event_board/activity_feed/EventActivityFeed.vue'
import MobileScreenTitle from '@/components/event_board/mobile/MobileScreenTitle.vue'
</script>

<style scoped lang="scss">
.event-activity-feed-mobile {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: 0 $spacing-m;
}
</style>
