<template>
  <div class="reservations-wrapper">
    <div class="reservations" ref="reservationsContainer">
      <div v-if="$slots.firstCell">
        <slot name="firstCell" />
      </div>
      <template v-for="reservation in reservations" :key="reservation.id">
        <ReservationCard :reservation="reservation" :compactMode="isMultiRow" />
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Reservation } from '@/data/domain/Reservation'
import ReservationCard from '@/components/event_board/reservation_card/ReservationCard.vue'
import { useGridRowsDetection } from '@/composable/useGridRowsDetection'
import { ref } from 'vue'

defineProps<{
  reservations?: Reservation[]
}>()

const reservationsContainer = ref<HTMLElement | null>(null)
const { isMultiRow } = useGridRowsDetection(reservationsContainer)
</script>

<style scoped lang="scss">
$reservation-card-width: 300px;

.reservations-wrapper {
  justify-content: center;
}

.reservations {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax($reservation-card-width, 1fr));
  grid-auto-rows: minmax(auto, max-content);
  gap: $spacing-l;
  justify-content: space-around;

  > * {
    margin: 0 auto;
    width: 100%;
  }
}
</style>
