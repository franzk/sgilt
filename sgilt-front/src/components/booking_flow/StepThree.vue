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
      <h3>📜 Récapitulatif de votre réservation</h3>

      <div class="infos">
        <div class="recap-card">
          <p class="location">
            <IconPlace /> <span>{{ reservationStore.location }}</span>
          </p>
        </div>

        <div class="recap-card datetime">
          <p>📆 {{ dateReservation }} à {{ reservationStore.timeReservation }}</p>
        </div>
      </div>

      <PartnerItem :partner="reservationStore.partner!!" hidePrice />

      <div class="recap-card">
        <p class="price">💰 <strong>Tarif :</strong> 1500 €</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useReservationStore } from '@/stores/reservation.store'
import IconPlace from '../icons/IconPlace.vue'
import PartnerItem from '../partner/PartnerItem.vue'
import dayjs from 'dayjs'
import { computed } from 'vue'

const reservationStore = useReservationStore()

const dateReservation = computed(() => dayjs(reservationStore.dateReservation).format('DD/MM/YYYY'))

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
  flex: 1;
  border-right: 2px solid #ddd;
  padding-right: 2rem;
}

.recap-section {
  flex: 2;
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

.recap-card {
  background: #f8f8f8;
  padding: 1rem;
  border-radius: $border-radius-m;
  box-shadow: $box-shadow;
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  p {
    margin: 0;
    padding: 0;
  }
  &:hover {
    background: #f1f1f1; // Légère variation du fond
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15); // Ombre plus marquée
    transform: translateY(-2px); // Léger soulèvement
    cursor: pointer;
  }
}

.infos {
  display: flex;
  flex-direction: row;
  gap: 1rem;
  .datetime {
    justify-content: center;
    flex: 1;
  }
}

.location {
  height: 2rem;
  display: flex;
  align-items: start;
  align-items: center;
  svg {
    width: 1.5rem;
    height: 1.5rem;
  }
  span {
    flex: 1;
  }
}
.price {
  margin-top: $spacing-l;
}

.captcha-section {
  margin-top: 1rem;
  text-align: center;
}

.submit-btn {
  background: gold;
  padding: 0.8rem;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  width: 100%;
}
</style>
