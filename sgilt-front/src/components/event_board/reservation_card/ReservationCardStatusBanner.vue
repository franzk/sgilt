<template>
  <div class="reservation-status" :style="statusStyle">
    <div class="status-wrapper">
      <!-- state -->
      <p class="status-state">✔️ {{ status?.state }}</p>
      <!-- action to do in this step -->
      <p class="status-action" :class="{ 'action-color': status?.actionColor }">
        <span><SgiltIcon v-if="status" :icon="status?.icon" /></span>
        <span class="status-action-label">{{ status?.action }}</span>
      </p>
    </div>
    <p class="status-subtext">{{ status?.subtext }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'
import type { ReservationStatusKey } from '@/types/ReservationStatus'
import { useReservationStatusColor } from '@/composable/useReservationStatusColor'
import { useReservationStatusStore } from '@/stores/reservation-status.store'

const props = defineProps<{
  statusKey?: ReservationStatusKey
  createdAt?: Date
}>()

const reservationStatusColor = useReservationStatusColor()
const statusStyle = computed(() =>
  reservationStatusColor.statusColorStyle('--status-color', props.statusKey, props.createdAt),
)

// status color and action color
const reservationStatusStore = useReservationStatusStore()

const status = computed(() =>
  props.statusKey ? reservationStatusStore.getStatus(props.statusKey) : undefined,
)

/*const statusColor = computed(() => status?.value?.color || 'transparent')
const actionColor = computed(() => status?.value?.actionColor || 'transparent') */
</script>

<style scoped lang="scss">
// $status-color: v-bind(statusColor);
// $action-color: v-bind(actionColor);

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
    padding: 0 $spacing-s $spacing-s $spacing-s;
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
      gap: $spacing-s;
      font-weight: 700;

      svg {
        height: 2rem;
        aspect-ratio: 1;
      }

      .status-action-label {
        line-height: 1.1;
      }
    }

    .action-color {
      border-radius: $border-radius-l;
      z-index: 0;
    }

    // background = faded status color
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: var(--status-color);
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
