<template>
  <div class="celebration-container">
    <!-- 🎊 Confettis -->
    <canvas ref="confettiCanvas" style="height: 0"></canvas>

    <img class="celebration-image" src="@/assets/images/home_bg.jpg" />

    <!-- ✅ Titre de validation -->
    <div class="celebration-content">
      <h1>{{ $t('booking-flow.step-4.title') }}</h1>
      <p>
        {{ $t('booking-flow.step-4.subtitle.start') }}
        <strong>{{ reservationStore.partner?.title }}</strong>
        <span v-html="$t('booking-flow.step-4.subtitle.end')" />&nbsp;
        <span
          ><strong>{{ $t('booking-flow.step-4.subtitle.stay-tuned') }}</strong></span
        >
      </p>

      <!-- 📅 Détails de la réservation -->
      <div class="recap-details">
        <p>
          <strong>{{ $t('booking-flow.step-4.labels.location') }}</strong>
          {{ reservationStore.location }}
        </p>
        <p>
          <strong>{{ $t('booking-flow.step-4.labels.date') }}</strong> {{ dateReservation }} à
          {{ reservationStore.timeReservation }}
        </p>
        <p>
          <strong>{{ $t('booking-flow.step-4.labels.price') }}</strong>
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

  <!-- 🎉 Boutons de fin -->
  <div class="step-4-buttons">
    <SgiltButton @click="console.log">
      {{ $t('booking-flow.step-4.cta.btn-event-board') }}
    </SgiltButton>
    <SgiltButton @click="$emit('close')" variant="secondary">
      {{ $t('booking-flow.step-4.cta.btn-close') }}
    </SgiltButton>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import confetti from 'canvas-confetti'
import { useReservationStore } from '@/stores/reservation.store'
import dayjs from 'dayjs'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'

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
  background: linear-gradient(135deg, #ffdf7e, #ffbe0b);
  border-radius: $border-radius-m $border-radius-m 0 0;
  position: relative;
  line-height: $line-height-base;

  .celebration-image {
    width: 100%;
    max-height: 200px;
    object-fit: cover;
    border-top-left-radius: $border-radius-m;
    border-top-right-radius: $border-radius-m;
  }

  .celebration-content {
    padding: $spacing-xl;
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
  }
}

p {
  font-size: $font-size-h3;
  line-height: $line-height-h3;
  color: #444;
}

.recap-details {
  @include respond-to(mobile) {
    text-align: left;
  }
  background: white;
  padding: $spacing-m;
  border-radius: $border-radius-sm;
  box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.1);
}

.step-4-buttons {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  background: $color-white;
  padding: $spacing-l;
  box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.2);
}
</style>
