<template>
  <div class="activity-item swiper-no-swiping">
    <!-- header : partner avatar and name -->
    <div class="activity-header">
      <img :src="activity.partnerAvatarUrl" class="partner-avatar" />
      <div class="activity-title">
        <span class="activity-partner">{{ activity.partnerName }}</span>
        <span class="activity-date" v-if="mobile">{{ formatedDate }}</span>
      </div>
    </div>
    <!-- content : activity description -->
    <p
      class="activity-content"
      v-html="
        $t(`event.activities.${activity.eventActivityTypeId}.content`, {
          partnerName: 'thePartnerName',
        }).replace('thePartnerName', `<i>${activity.partnerName}</i>`)
      "
    />

    <!-- date : activity date -->
    <p v-if="!mobile" class="activity-date">{{ formatedDate }}</p>
  </div>
</template>

<script setup lang="ts">
import type { EventActivity } from '@/data/domain/EventActivity'
import { useEventActivityStore } from '@/stores/event-activity.store'
import dayjs from 'dayjs'
import { computed } from 'vue'

const props = defineProps<{
  activity: EventActivity
  mobile?: boolean
}>()

const formatedDate = computed(() => {
  return dayjs(props.activity.date).format('DD/MM/YYYY HH:mm:ss')
})

// activity color
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
  padding: $spacing-s;
  box-shadow: $box-shadow;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);

  // faded background
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
      display: flex;
      flex-direction: column;
      gap: $spacing-xs;
      .activity-partner {
        font-weight: 700;
      }
    }

    .partner-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
    }
  }

  .activity-content {
    font-size: $font-size-base;
    text-align: left;
  }

  .activity-date {
    flex: 1;
    text-align: left;
    font-size: $font-size-base;
    font-style: italic;
  }
}
</style>
