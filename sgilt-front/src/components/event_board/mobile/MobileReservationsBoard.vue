<template>
  <div class="reservations-board-mobile">
    <MobileScreenTitle icon="List" label="Réservations" />
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
import MobileScreenTitle from '@/components/event_board/mobile/MobileScreenTitle.vue'
import { useEventStore } from '@/stores/event.store'
import { computed } from 'vue'

const eventStore = useEventStore()
const reservations = computed(() => eventStore.sgiltEvent?.reservations || [])
</script>

<style scoped lang="scss">
.reservations-board-mobile {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: 0 $spacing-m;
}

.reservations-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
</style>
