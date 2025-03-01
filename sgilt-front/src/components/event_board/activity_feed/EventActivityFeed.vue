<template>
  <div class="event-activity-feed">
    <h3 class="feed-title">📰 Activité récente</h3>
    <div v-if="activities.length === 0" class="empty-feed">Aucune activité pour le moment.</div>

    <ul class="activity-list">
      <EventActivityItem v-for="activity in activities" :key="activity.id" :activity="activity" />
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
