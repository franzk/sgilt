<template>
  <div class="event-help-item-oneline" v-if="oneline">
    <span class="help-item-icon">
      <SgiltIcon :icon="icon" />
    </span>
    <span class="help-item-title">
      {{ title }}
    </span>
    <span class="help-item-description">
      {{ content }}
    </span>
  </div>
  <div class="event-help-item-twolines" v-else>
    <div class="first-line">
      <span class="help-item-icon">
        <SgiltIcon :icon="icon" />
      </span>
      <span class="help-item-title">
        {{ title }}
      </span>
    </div>
    <div class="second-line">
      <span class="help-item-description">
        {{ content }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

defineProps<{
  icon: string
  title: string
  content: string
  oneline?: boolean
}>()
</script>

<style scoped lang="scss">
.event-help-item-oneline {
  display: flex;
  gap: $spacing-xs;
  align-items: center;
}

.event-help-item-twolines {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
  .first-line {
    display: flex;
    gap: $spacing-s;
    align-items: center;
  }
  .second-line {
    text-align: left;
  }
}

.help-item-icon {
  svg {
    height: 1.2rem;
    aspect-ratio: 1;
  }
}

.help-item-title {
  font-weight: 700;
  text-align: left;
}
</style>
