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
import { ref } from 'vue'
import { findAllEventActivities } from '@/data/repository/EventActivityRepository'
import EventActivityItem from '@/components/event_board/activity_feed/EventActivityItem.vue'

const activities = ref(findAllEventActivities())
</script>

<style scoped lang="scss">
.event-feed {
  border-left: 1px solid $color-divider;
}

.feed-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  list-style: none;
}
</style>
