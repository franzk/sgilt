<template>
  <div class="reservations-wrapper">
    <div class="reservations">
      <template v-for="reservation in reservations" :key="reservation.id">
        <ReservationCard :reservation="reservation" />
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Reservation } from '@/data/domain/Reservation'
import ReservationCard from '@/components/event_board/reservation_card/ReservationCard.vue'

defineProps<{
  reservations?: Reservation[]
}>()
</script>

<style scoped lang="scss">
.reservations-wrapper {
  justify-content: center;
}
.reservations {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 300px));
  grid-auto-rows: minmax(auto, max-content);
  gap: $spacing-l;
  justify-content: space-around;
  > * {
    margin: 0 auto;
    width: 100%;
  }
}
</style>
