<template>
  <div class="event-activity-feed">
    <ul class="feed-list">
      <li v-for="activity in activities" :key="activity.id" class="feed-item">
        <EventActivityItem :activity="activity" />
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import EventActivityItem from '@/components/event_board/activity_feed/EventActivityItem.vue'
import type { EventActivity } from '@/data/domain/EventActivity'

defineProps<{
  activities: EventActivity[]
}>()
</script>

<style scoped lang="scss">
.feed-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-mm;
  list-style: none;
  margin: 0;
  /*&.mobile-layout {
    padding: 0;
  }*/
}
</style>
