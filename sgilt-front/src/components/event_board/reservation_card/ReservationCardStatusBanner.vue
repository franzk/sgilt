<template>
  <div class="reservation-status">
    <div class="status-wrapper">
      <!-- state -->
      <p class="status-state">✔️ {{ status?.state }}</p>
      <!-- action to do in this step -->
      <p class="status-action">
        <span><SgiltIcon v-if="status" :icon="status?.icon" /></span>
        <span class="status-action-label">{{ status?.action }}</span>
      </p>
    </div>
    <p class="status-subtext">{{ status?.subtext }}</p>
  </div>
</template>

<script setup lang="ts">
import { useReservationStatusStore } from '@/stores/reservation-status.store'
import { computed } from 'vue'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

const props = defineProps<{
  statusKey?: string
}>()

const reservationStatusStore = useReservationStatusStore()

const status = computed(() =>
  props.statusKey ? reservationStatusStore.getStatus(props.statusKey) : undefined,
)
const statusColor = computed(() => status?.value?.color || 'transparent')
</script>

<style scoped lang="scss">
$status-color: v-bind(statusColor);

.reservation-status {
  p {
    margin: 0;
  }

  display: flex;
  flex-direction: column;
  position: relative;
  gap: $spacing-s;

  .status-wrapper {
    display: flex;
    flex-direction: column;
    position: relative;
    align-items: start;
    text-align: left;
    width: 100%;
    box-shadow: $box-shadow;

    .status-state {
      font-weight: 500;
      color: $color-primary;
      line-height: 1.5;
      padding: $spacing-xs $spacing-s 0 $spacing-s;
    }

    .status-action {
      display: flex;
      flex-direction: row;
      align-items: center;
      padding: 0 $spacing-s;
      gap: 0.5rem;
      font-weight: 700;
      svg {
        height: 2rem;
        aspect-ratio: 1;
      }
      .status-action-label {
        line-height: 1.1;
      }
    }

    // background = faded status color
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: $status-color;
      opacity: 0.5;
    }
    * > * {
      z-index: 0;
    }
  }
}

.status-subtext {
  font-style: italic;
  padding: 0 $spacing-s;
}
</style>
