<template>
  <div class="reservation-list">
    <ReservationCard
      v-for="reservation in reservations"
      :key="reservation.id"
      :reservation="reservation"
      @click="$emit('open', reservation.id)"
    />
  </div>
</template>

<script setup lang="ts">
// Liste des réservations d'un événement (page rubrique, liste des réservations). La page décide
// de l'URL de la réservation ouverte.
import ReservationCard from '~/components/app/ReservationCard.vue'
import type { ReservationSummary } from '~/data/reservation/domain/ReservationSummary'

defineProps<{
  reservations: ReservationSummary[]
}>()

defineEmits<{
  open: [reservationId: string]
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.reservation-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}
</style>
