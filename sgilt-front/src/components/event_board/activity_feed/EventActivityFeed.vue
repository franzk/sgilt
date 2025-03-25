<template>
  <div class="event-activity-feed">
    <ul :class="['feed-list', { 'mobile-layout': mobile }]">
      <li v-for="activity in activities" :key="activity.id" class="feed-item">
        <EventActivityItem :activity="activity" :mobile="mobile" />
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import EventActivityItem from '@/components/event_board/activity_feed/EventActivityItem.vue'
import { useEventStore } from '@/stores/event.store'
import { computed } from 'vue'

const eventStore = useEventStore()
const activities = computed(() => eventStore.activities)

defineProps<{
  mobile?: boolean
}>()
</script>

<style scoped lang="scss">
.feed-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-mm;
  list-style: none;
  margin: 0;

  &.mobile-layout {
    padding: 0;
  }
}
</style>
