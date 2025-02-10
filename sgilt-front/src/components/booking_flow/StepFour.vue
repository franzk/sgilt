<template>
  <div class="celebration-container">
    <!-- 🎊 Confettis -->
    <canvas ref="confettiCanvas" style="height: 0"></canvas>

    <img class="celebration-image" src="@/assets/images/home_bg.jpg" />

    <!-- ✅ Titre de validation -->
    <div class="celebration-content">
      <h1>{{ $t('booking-flow.step-4.title') }}</h1>
      <p>
        {{ $t('booking-flow.step-4.subtitle-start') }}
        <strong>{{ reservationStore.partner?.title }}</strong>
        <span v-html="$t('booking-flow.step-4.subtitle-end')" />&nbsp;
        <span
          ><strong>{{ $t('booking-flow.step-4.stay-tuned') }}</strong></span
        >
      </p>

      <!-- 📅 Détails de la réservation -->
      <div class="recap-details">
        <p>
          📍 <strong>{{ $t('booking-flow.step-4.location-label') }}</strong>
          {{ reservationStore.location }}
        </p>
        <p>
          📅 <strong>{{ $t('booking-flow.step-4.date-label') }}</strong> {{ dateReservation }} à
          {{ reservationStore.timeReservation }}
        </p>
        <p>
          💰 <strong>{{ $t('booking-flow.step-4.price-label') }}</strong>
          {{ reservationStore.totalPrice }} €
        </p>
      </div>

      <!-- next steps -->
      <div class="next-steps">
        <p class="highlight">{{ $t('booking-flow.step-4.next-steps.title') }}</p>
        <p>
          {{ $t('booking-flow.step-4.next-steps.message') }}
          <strong>{{ $t('booking-flow.step-4.next-steps.event-board') }}</strong
          >.
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import confetti from 'canvas-confetti'
import { useReservationStore } from '@/stores/reservation.store'
import dayjs from 'dayjs'

const reservationStore = useReservationStore()
const dateReservation = computed(() => dayjs(reservationStore.dateReservation).format('DD/MM/YYYY'))
const confettiCanvas = ref(null)

// 🎊 Fonction pour déclencher les confettis
const launchConfetti = () => {
  confetti({
    particleCount: 150,
    spread: 80,
    origin: { y: 0.6 }, // Ça part du haut de l’écran
    zIndex: 1000,
  })
}

// ⏳ Lance les confettis quand le composant est monté
onMounted(() => {
  setTimeout(() => {
    launchConfetti()
  }, 500)
})
</script>

<style scoped lang="scss">
.celebration-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  // padding: 3rem;
  background: linear-gradient(135deg, #ffdf7e, #ffbe0b);
  border-radius: 12px;
  box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.2);
  position: relative;
  line-height: $line-height-base;

  .celebration-image {
    width: 100%;
    max-height: 200px;
    object-fit: cover;
    border-top-left-radius: 12px;
    border-top-right-radius: 12px;
  }

  .celebration-content {
    padding: 3rem;
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
  }
}

h1 {
  font-size: 2.5rem;
  color: #333;
}

p {
  font-size: 1.2rem;
  line-height: 1.2rem;
  color: #444;
}

.recap-details {
  // margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 10px;
  box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.1);
}
</style>
