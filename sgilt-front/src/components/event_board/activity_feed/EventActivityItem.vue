<template>
  <div class="activity-item">
    <div class="activity-icon">
      <SgiltIcon v-if="activity.eventActivityType.icon" :icon="activity.eventActivityType.icon" />
    </div>
    <div class="activity-content">
      <p class="activity-title">
        {{
          $t(`event-activities.${activity.eventActivityType.id}.title`, {
            partnerName: activity.partnerName,
          })
        }}
      </p>
      <p class="activity-date">{{ formatDate(activity.date) }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { EventActivity } from '@/data/domain/EventActivity'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

defineProps<{
  activity: EventActivity
}>()

const formatDate = (date: Date) => {
  const now = new Date()
  const diff = Math.floor((now.getDate() - date.getDate()) / 1000 / 60 / 60 / 24)
  if (diff === 0) return "Aujourd'hui"
  if (diff === 1) return 'Hier'
  return `Il y a ${diff} jours`
}
</script>

<style scoped lang="scss">
p {
  margin: 0;
}

.activity-item {
  display: flex;
  gap: $spacing-s;

  .activity-icon {
    flex: 0 0 auto;
    svg {
      height: auto;
    }
  }
}
</style>
