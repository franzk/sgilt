<template>
  <li class="activity-item">
    <!-- Avatar -->
    <div class="partner-avatar">
      <img :src="activity.partnerAvatarUrl" :alt="activity.partnerName" />
    </div>

    <!-- Contenu -->
    <div class="activity-content">
      <div class="activity-header">
        <span class="activity-icon" :style="{ color: activity.eventActivityType.color }">
          <SgiltIcon
            v-if="activity.eventActivityType.icon"
            :icon="activity.eventActivityType.icon"
          />
        </span>
        <p class="activity-title">
          {{
            $t(`event-activities.${activity.eventActivityType.id}.title`, {
              partnerName: activity.partnerName,
            })
          }}
        </p>
      </div>
      <p class="activity-message">
        {{
          $t(`event-activities.${activity.eventActivityType.id}.content`, { ...activity.payload })
        }}
      </p>
      <span class="activity-date">{{ activity.date }}</span>
    </div>
  </li>
</template>

<script setup lang="ts">
import type { EventActivity } from '@/data/domain/EventActivity'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

defineProps<{
  activity: EventActivity
}>()
</script>

<style scoped lang="scss">
.activity-item {
  display: flex;
  align-items: center;
  gap: $spacing-m;
  padding: $spacing-s;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease-in-out;

  &:hover {
    background: lighten($color-secondary, 10%);
  }
}

/* 🎭 Avatar du partenaire */
.partner-avatar {
  width: 3rem;
  height: 3rem;
  flex-shrink: 0;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

/* 📝 Contenu */
.activity-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.activity-header {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
}

.activity-icon {
  font-size: 1.2rem;
}

/* 🏷️ Titre */
.activity-title {
  font-weight: bold;
  color: $color-primary;
  font-size: $font-size-base;
}

/* 💬 Message */
.activity-message {
  // font-size: $font-size-small;
  // color: $color-text-light;
}

/* 📆 Date */
.activity-date {
  font-size: 0.8rem;
  // color: $color-text-light;
  margin-top: $spacing-xs;
}
</style>
