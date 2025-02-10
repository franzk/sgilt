<template>
  <div class="recap-container">
    <aside class="timeline-bar">
      <RecapTimeLine />
    </aside>

    <!-- Colonne droite : Récap -->
    <div class="recap-section">
      <h3 class="recap-section-title">📜 Récapitulatif de votre réservation</h3>

      <!-- infos : location & date-time -->
      <div class="infos">
        <RecapCard :title="reservationStore.location!!" icon="Place" class="location" />
        <RecapCard :title="dateTimeReservation" icon="Calendar" class="datetime" />
      </div>

      <!-- booked partner -->
      <PartnerItem :partner="reservationStore.partner!!" hidePrice />

      <!-- price section -->
      <template v-if="!!priceDetail">
        <RecapCard icon="Price" :title="priceTitle">
          <span v-html="priceDetail" />
        </RecapCard>
      </template>
      <template v-else>
        <RecapCard icon="Price" :title="priceTitle" />
      </template>

      <!-- user infos -->
      <RecapCard icon="Mail" :title="reservationStore.email!!" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useReservationStore } from '@/stores/reservation.store'
import RecapTimeLine from '@/components/booking_flow/RecapTimeLine.vue'
import PartnerItem from '@/components/partner/PartnerItem.vue'
import RecapCard from '@/components/booking_flow/RecapCard.vue'
import dayjs from 'dayjs'
import { computed } from 'vue'

const reservationStore = useReservationStore()

// date & time
const dateTimeReservation = computed(
  () =>
    `${dayjs(reservationStore.dateReservation).format('DD/MM/YYYY')} à ${reservationStore.timeReservation}`,
)

// price
const priceTitle = `Tarif : <strong>${reservationStore.totalPrice} €</strong>`
const priceDetail = computed(() => {
  if (reservationStore.quantity) {
    if (reservationStore.quantity) {
      return `
      💳 ${reservationStore.price?.title}

      ${reservationStore.price?.price} € / ${reservationStore.price?.unity} * ${reservationStore.quantity} ${reservationStore.price?.unity} = <strong>${reservationStore.totalPrice} €</strong>
      `
    }
  }
  return null
})
</script>

<style scoped lang="scss">
.recap-container {
  display: flex;
  gap: $spacing-l;
  padding: $spacing-m;

  .timeline-bar {
    width: 35rem;
    height: auto;
  }

  .recap-section {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;

    .recap-section-title {
      margin-bottom: 0.3rem;
    }

    .infos {
      display: flex;
      flex-direction: row;
      gap: $spacing-m;

      .datetime {
        width: 12rem;
        justify-content: center;
      }

      .location {
        flex: 1;
      }
    }
  }
}
</style>
