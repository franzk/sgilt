<template>
  <div class="recap-container">
    <!-- Colonne gauche : Timeline -->
    <div class="timeline-section">
      <h3>⏳ Comment ça marche ?</h3>
      <div class="timeline">
        <div v-for="(step, index) in steps" :key="index" class="timeline-step">
          <div class="step-number" :class="{ active: index === currentStep }">
            {{ index + 1 }}
          </div>
          <p class="step-label">{{ step.label }}</p>
        </div>
      </div>
    </div>

    <!-- Colonne droite : Récap -->
    <div class="recap-section">
      <h3 class="recap-section-title">📜 Récapitulatif de votre réservation</h3>

      <div class="infos">
        <RecapCard :title="reservationStore.location!!" icon="Place" class="location" />
        <RecapCard :title="dateTimeReservation" icon="Calendar" class="datetime" />
      </div>

      <PartnerItem :partner="reservationStore.partner!!" hidePrice />

      <RecapCard icon="Price" :title="priceTitle"><span v-html="priceDetail" /></RecapCard>

      <RecapCard icon="Mail" :title="reservationStore.email!!" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useReservationStore } from '@/stores/reservation.store'
import PartnerItem from '../partner/PartnerItem.vue'
import RecapCard from './RecapCard.vue'
import dayjs from 'dayjs'
import { computed } from 'vue'

const reservationStore = useReservationStore()

const dateTimeReservation = computed(
  () =>
    `${dayjs(reservationStore.dateReservation).format('DD/MM/YYYY')} à ${reservationStore.timeReservation}`,
)

const totalPrice = computed(() => {
  if (reservationStore.quantity) {
    return (reservationStore.price?.price || 0) * reservationStore.quantity
  }
  return reservationStore.price?.price
})

const priceTitle = `Tarif : <strong>${totalPrice.value} €</strong>`
const priceDetail = computed(() => {
  if (reservationStore.quantity) {
    if (reservationStore.quantity) {
      return `
      💳 ${reservationStore.price?.title}

      ${reservationStore.price?.price} € / ${reservationStore.price?.unity} * ${reservationStore.quantity} ${reservationStore.price?.unity} = <strong>${totalPrice.value} €</strong>
      `
    }
  }
  return ''
})

const currentStep = 0
const steps = [
  { label: 'Vous envoyez votre demande' },
  { label: 'Le partenaire vous contacte' },
  { label: 'Vous validez et payez' },
  { label: '🎉 C’est réservé !' },
]
</script>

<style scoped lang="scss">
.recap-container {
  display: flex;
  gap: 2rem;
  padding: 1rem;
}

.timeline-section {
  width: 22rem;
  border-right: 2px solid #ddd;
  padding-right: 2rem;
}

.recap-section {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  .recap-section-title {
    margin-bottom: 0.3rem;
  }
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.timeline-step {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.step-number {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  background: #ccc;
  color: #fff;
}

.step-number.active {
  background: gold;
  box-shadow: 0 0 10px gold;
}

.infos {
  display: flex;
  flex-direction: row;
  gap: 1rem;
  .datetime {
    width: 14rem;
    justify-content: center;
  }
  .location {
    flex: 1;
  }
}

.location {
  flex: 1;
}
</style>
