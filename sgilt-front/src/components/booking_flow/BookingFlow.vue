<template>
  <div class="booking-flow-modal" v-if="showModal" @click.self="closeModal">
    <div :class="['event-booking', { 'as-modal': isModal, 'full-height': isMobile }]">
      <div class="container">
        <!-- Bouton de fermeture -->
        <button v-if="isModal" class="close-btn" @click="closeModal">✖</button>

        <!-- Header Full Width avec Dégradé -->
        <header class="booking-header">
          <h2>{{ $t(`booking-flow.step-${step + 1}.title`) }}</h2>
          <p>{{ $t(`booking-flow.step-${step + 1}.subtitle`) }}</p>
          <BookingProgressBar :stepCount="4" v-model="step" @stepChange="goToStep" />
        </header>

        <!-- Contenu dynamique -->
        <div class="content">
          <StepOne v-if="step === 0" />
          <StepTwo v-if="step === 1" />
          <StepThree v-if="step === 2" />
          <StepFour v-if="step === 3" />
        </div>

        <!-- Footer Sticky -->
        <div class="modal-footer">
          <SgiltButton v-if="step > 0" @click="step--" variant="secondary"
            >&larr; Retour</SgiltButton
          >
          <SgiltButton class="btn-submit" @click="nextStep">
            {{ step === 3 ? 'Finaliser' : 'Continuer →' }}
          </SgiltButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'
import StepOne from '@/components/booking_flow/StepOne.vue'
import StepTwo from '@/components/booking_flow/StepTwo.vue'
// import StepThree from '@/components/booking_flow/StepThree.vue'
// import StepFour from '@/components/booking_flow/StepFour.vue'
import { useReservationStore } from '@/stores/reservation.store'
import BookingProgressBar from '@/components/booking_flow/BookingProgressBar.vue'

const reservationStore = useReservationStore()

defineProps<{ showModal: boolean }>()

const emit = defineEmits<{ (e: 'close'): void }>()

const closeModal = () => emit('close')

const step = ref(0)

const route = useRoute()
const isModal = computed(() => !route.path.includes('/event-booking'))
const isMobile = ref(false)

onMounted(() => {
  isMobile.value = window.innerWidth < 768
})

const nextStep = () => {
  if (step.value === 0 && reservationStore.checkStepOne()) {
    step.value++
  }
}

const goToStep = (index: number) => {
  if (index < step.value) {
    step.value = index
  }
}
</script>

<style scoped lang="scss">
/* --- MODAL GÉNÉRALE --- */
.booking-flow-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.event-booking {
  background: $color-white;
  border-radius: $border-radius-m;
  box-shadow: 0 8px 20px $shadow-m;
  width: 100%;
  max-width: 700px;
  margin: auto;
  position: relative;

  &.as-modal {
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
    max-height: 85vh;
    overflow-y: auto;
  }

  &.full-height {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: white;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
}

/* --- HEADER FULL WIDTH AVEC DÉGRADÉ --- */
.booking-header {
  text-align: center;
  padding: 1.5rem 0;
  background: linear-gradient(70deg, rgba(255, 191, 0, 0.9), rgba(255, 255, 255, 0));
  width: 100%;
  border-radius: 8px 8px 0 0;
}

/* --- BARRE DE PROGRESSION DANS LE HEADER --- */
/*.progress-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 1rem;
  padding: 0 1rem;
}

.step {
  display: flex;
  align-items: center;
  position: relative;
  flex: 1;
}

.step-circle {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  background: $color-secondary;
  transition: 0.3s;
}

.step-line {
  flex: 1;
  height: 3px;
  background: $shadow-s;
}

.step.active .step-circle {
  background: $color-accent;
  color: white;
  box-shadow: 0 0 10px rgba(255, 191, 0, 0.5);
}

.step.completed .step-circle {
  background: $color-primary;
  color: white;
}

.step.completed .step-line {
  background: $color-primary;
}*/

/* --- BOUTON DE FERMETURE --- */
.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.content {
  padding: $spacing-m;
}

/* --- FOOTER --- */
.modal-footer {
  position: sticky;
  bottom: 0;
  background: white;
  padding: 1rem;
  border-top: 1px solid $shadow-s;
  display: flex;
  gap: $spacing-m;

  .btn-submit {
    flex: 1;
    background: $color-accent;
    color: white;
    font-weight: bold;
  }
}
</style>
