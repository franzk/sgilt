<template>
  <div class="reservations-board-mobile">
    <h2>Reservations</h2>
    <div class="reservations-container">
      <MobileReservationCard
        v-for="reservation in reservations"
        :key="reservation.id"
        :reservation="reservation"
        class="reservation-card"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import MobileReservationCard from '@/components/event_board/mobile/MobileReservationCard.vue'
import { useEventStore } from '@/stores/event.store'
import { computed } from 'vue'

const eventStore = useEventStore()
const reservations = computed(() => eventStore.sgiltEvent?.reservations || [])
</script>

<style scoped lang="scss">
.reservations-board-mobile {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1rem;
}

h2 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.reservations-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
</style>
