<template>
  <div class="activity-item">
    <div class="activity-header">
      <img :src="activity.partnerAvatarUrl" class="partner-avatar" />
      <span class="activity-title">
        {{ activity.partnerName }}
      </span>
    </div>

    <p
      class="activity-content"
      v-html="
        $t(`event.activities.${activity.eventActivityTypeId}.content`, {
          partnerName: 'thePartnerName',
        }).replace('thePartnerName', `<i>${activity.partnerName}</i>`)
      "
    />

    <p class="activity-date">{{ formatDate(activity.date) }}</p>
  </div>
</template>

<script setup lang="ts">
import type { EventActivity } from '@/data/domain/EventActivity'
import { useEventActivityStore } from '@/stores/event-activity.store'
import { computed } from 'vue'

const props = defineProps<{
  activity: EventActivity
}>()

const formatDate = (date: Date) => {
  const now = new Date()
  const diff = Math.floor((date.getDate() - now.getDate()) / 1000 / 60 / 60 / 24)
  if (diff === 0) return "Aujourd'hui"
  if (diff === 1) return 'Hier'
  return `Il y a ${diff} jours`
}

const eventActivityStore = useEventActivityStore()
const backgroundColor = computed(() =>
  eventActivityStore.getColor(props.activity.eventActivityTypeId),
)
</script>

<style scoped lang="scss">
$background-color: v-bind(backgroundColor);

p {
  margin: 0;
}

.activity-item {
  display: flex;
  flex-direction: column;
  position: relative;
  gap: $spacing-s;
  border-bottom: 1px solid $color-divider;
  padding: $spacing-s;
  box-shadow: $box-shadow;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  // background-color: $background-color;

  // background = faded status color
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: $background-color;
    opacity: 0.4;
    z-index: -1;
  }

  .activity-header {
    display: flex;
    flex-direction: row;
    gap: $spacing-s;
    align-items: center;

    .activity-title {
      font-weight: 700;
    }

    .partner-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
    }
  }

  .activity-content {
    font-size: $font-size-base;
  }

  .activity-date {
    font-size: $font-size-base;
    color: $color-subtext;
    font-style: italic;
  }
}
</style>
