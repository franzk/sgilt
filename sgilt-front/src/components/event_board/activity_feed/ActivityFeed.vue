<template>
  <div class="activity-feed">
    <h3 class="feed-title">Journal d'activité</h3>
    <div class="feed-container">
      <template v-for="activity in activities" :key="activity.id">
        <div :class="['feed-item', activity.type]">
          <div class="feed-avatar">
            <img v-if="activity.avatar" :src="activity.avatar" alt="avatar" />
            <SgiltIcon v-else :icon="activity.icon" />
          </div>
          <div class="feed-content">
            <p class="feed-title">
              {{ t(activity.i18nKey, { partnerName: activity.partnerName }) }}
            </p>
            <p class="feed-message">{{ t(activity.i18nDescription) }}</p>
          </div>
          <span class="feed-time">{{ formatTime(activity.timestamp) }}</span>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useReservationActivityStore } from '@/stores/event-activity-type.store'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'
import { useI18n } from 'vue-i18n'
// import { formatTime } from '@/utils/dateUtils'

const { t } = useI18n()
const activityStore = useReservationActivityStore()
const activities = computed(() => activityStore.activities)
</script>

<style scoped lang="scss">
.activity-feed {
  background: $color-secondary;
  padding: $spacing-m;
  border-radius: $border-radius-m;
  box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  display: flex;
  flex-direction: column;
}

.feed-title {
  font-size: $font-size-h3;
  font-weight: 600;
  color: $color-primary;
  margin-bottom: $spacing-m;
}

.feed-container {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.feed-item {
  display: flex;
  align-items: center;
  background: $color-white;
  padding: $spacing-s;
  border-radius: $border-radius-xs;
  box-shadow: 0px 1px 5px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease-in-out;
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.15);
  }
}

.feed-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: $spacing-s;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.feed-content {
  flex: 1;
  .feed-title {
    font-size: $font-size-base;
    font-weight: 600;
    color: $color-primary;
  }
  .feed-message {
    font-size: $font-size-s;
    color: $color-subtext;
  }
}

.feed-time {
  font-size: $font-size-xs;
  color: $color-subtext;
}
</style>
