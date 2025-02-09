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
        <!--div class="recap-card location">
          <IconPlace /> <span>{{ reservationStore.location }}</span>
        </!--div>

        <div-- class="recap-card datetime">
          <IconClock />
          <span>{{ dateReservation }} à {{ reservationStore.timeReservation }}</span>
        </div-->
        <RecapCard :text="reservationStore.location!!" class="location"><IconPlace /></RecapCard>
        <RecapCard :text="dateTimeReservation" class="datetime"><IconCalendar /></RecapCard>
      </div>

      <PartnerItem :partner="reservationStore.partner!!" hidePrice />

      <!--div class="recap-card">
        <p class="price">💰 <strong>Tarif :</strong> 1500 €</p>
      </!--div-->
      <RecapCard text="Tarif : 1500 €"><IconPrice /></RecapCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useReservationStore } from '@/stores/reservation.store'
import IconPlace from '../icons/IconPlace.vue'
import PartnerItem from '../partner/PartnerItem.vue'
import RecapCard from './RecapCard.vue'
import dayjs from 'dayjs'
import { computed } from 'vue'
import IconCalendar from '../icons/IconCalendar.vue'
import IconPrice from '../icons/IconPrice.vue'

const reservationStore = useReservationStore()

const dateTimeReservation = computed(
  () =>
    `${dayjs(reservationStore.dateReservation).format('DD/MM/YYYY')} à ${reservationStore.timeReservation}`,
)

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

/*.recap-card {
  background: #f8f8f8;
  padding: $spacing-m;
  border-radius: $border-radius-m;
  box-shadow: $box-shadow;
  // margin-bottom: 1rem;
  display: flex;
  flex-direction: column;

  align-items: center;
  span {
    flex: 1;
  }

  gap: $spacing-m;
  p {
    margin: 0;
    padding: 0;
  }
  svg {
    width: 1.5rem;
    height: 1.5rem;
  }

  &:hover {
    background: #f1f1f1; // Légère variation du fond
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15); // Ombre plus marquée
    transform: translateY(-2px); // Léger soulèvement
    cursor: pointer;
  }
}*/

.infos {
  display: flex;
  flex-direction: row;
  gap: 1rem;
  .datetime {
    width: 11rem;
    justify-content: center;
    //flex: 1;
  }
  .location {
    flex: 1;
    // height: 2rem;
    /*display: flex;
  align-items: center;
  span {
    flex: 1;
  }*/
  }
}

.location {
  flex: 1;
  // height: 2rem;
  /*display: flex;
  align-items: center;
  span {
    flex: 1;
  }*/
}
/*.price {
  margin-top: $spacing-l;
}*/

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
