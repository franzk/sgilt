<template>
  <MobileScreenTitle icon="Bell" title="Activité" />

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
import MobileScreenTitle from '@/components/event_board/MobileScreenTitle.vue'

const activities = ref(findAllEventActivities())
</script>

<style scoped lang="scss">
.feed-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-mm;
  list-style: none;
  margin: 0;
  @include respond-to(mobile) {
    padding: 0 $spacing-l $spacing-xxxl $spacing-l;
  }
}
</style>
