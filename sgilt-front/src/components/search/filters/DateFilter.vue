<template>
  <div class="filter-date">
    <SgiltDatePicker language="fr" class="date-event" v-model="reservationStore.dateReservation" />
  </div>
</template>

<script setup lang="ts">
import SgiltDatePicker from '@/components/basics/inputs/SgiltDatePicker.vue'
import { useReservationStore } from '@/stores/reservation.store'

const reservationStore = useReservationStore()
</script>

<style scoped lang="scss"></style>
