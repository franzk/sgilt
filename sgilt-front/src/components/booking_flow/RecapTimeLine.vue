<template>
  <div class="timeline-wrapper">
    <div class="timeline">
      <div v-for="(step, index) in steps" :key="index" class="timeline-step">
        <div class="step-number" :class="{ active: index === currentStep }">
          {{ index + 1 }}
        </div>
        <p class="step-label">{{ step.label }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

const currentStep = 0

const steps = [
  { label: t('booking-flow.step-3.timeline.step-1') },
  { label: t('booking-flow.step-3.timeline.step-2') },
  { label: t('booking-flow.step-3.timeline.step-3') },
  { label: t('booking-flow.step-3.timeline.step-4') },
]
</script>

<style scoped lang="scss">
.timeline-wrapper {
  @include respond-to(mobile) {
    align-items: center;
    padding-right: 0;
  }

  padding-right: $spacing-l;
  display: flex;
  flex-direction: column;
  height: 100%;

  .timeline {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
    @include respond-to(mobile) {
      gap: 0;
    }

    .timeline-step {
      display: flex;
      align-items: center;
      gap: $spacing-s;
    }

    .step-number {
      width: 30px;
      height: 30px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      background: #ccc;
      color: #fff;

      &.active {
        background: gold;
        box-shadow: 0 0 10px gold;
      }
    }
  }
}
</style>
