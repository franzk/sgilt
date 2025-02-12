<template>
  <div class="live-recap">
    <LiveRecapItem
      v-if="reservationStore.location"
      :title="$t('booking-flow.step-4.labels.location')"
      :value="reservationStore.location"
    />

    <LiveRecapItem
      v-if="reservationStore.dateReservation || reservationStore.timeReservation"
      :title="$t('booking-flow.step-4.labels.date')"
      :value="dateTimeReservation"
    />

    <LiveRecapItem
      v-if="reservationStore.partner"
      title="Votre partenaire :"
      :value="reservationStore.partner.title"
    />

    <LiveRecapItem
      v-if="reservationStore.totalPrice"
      :title="$t('booking-flow.step-4.labels.price')"
      :value="`${reservationStore.totalPrice} €`"
    />

    <LiveRecapItem
      v-if="reservationStore.email"
      :title="$t('booking-flow.step-3.titles.email')"
      :value="reservationStore.email"
    />

    <LiveRecapItem
      v-if="reservationStore.message"
      :title="$t('booking-flow.step-3.titles.message')"
      :value="reservationStore.message"
    />
  </div>
</template>

<script setup lang="ts">
import { useReservationStore } from '@/stores/reservation.store'
import dayjs from 'dayjs'
import { computed } from 'vue'
import LiveRecapItem from './LiveRecapItem.vue'
const reservationStore = useReservationStore()

const dateTimeReservation = computed(() => {
  const date = dayjs(reservationStore.dateReservation).format('DD/MM/YYYY')
  if (reservationStore.timeReservation) {
    return `${date} à ${reservationStore.timeReservation}`
  } else {
    return date
  }
})
</script>

<style scoped lang="scss">
.live-recap {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}
</style>
