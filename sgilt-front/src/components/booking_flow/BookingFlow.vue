<template>
  <div class="booking-flow-modal" v-if="showModal" @click.self="closeModal">
    <div :class="['event-booking', { 'as-modal': isModal, 'full-height': isMobile }]">
      <div class="container">
        <!-- Close button-->
        <button v-if="isModal" class="close-btn" @click="closeModal">✖</button>

        <!-- Header -->
        <header class="booking-header">
          <h2>{{ $t(`booking-flow.step-${step}.title`) }}</h2>
          <p>{{ $t(`booking-flow.step-${step}.subtitle`) }}</p>
          <BookingProgressBar v-model="step" @stepChange="goToStep" />
        </header>

        <!-- Dynamic content -->
        <transition :name="stepAnimation" mode="out-in">
          <div class="content" :key="step">
            <StepOne v-if="step === 1" />
            <StepTwo v-if="step === 2" />
            <StepThree v-if="step === 3" />
            <StepFour v-if="step === 4" />
          </div>
        </transition>

        <!-- Sticky Footer -->
        <div class="modal-footer">
          <div class="navigation">
            <SgiltButton v-if="step > 1" @click="prevStep" variant="secondary"
              >&larr; Retour</SgiltButton
            >
            <SgiltButton class="btn-submit" @click="nextStep">
              {{ $t(`booking-flow.step-${step}.cta`) }}
            </SgiltButton>
          </div>
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
import StepThree from '@/components/booking_flow/StepThree.vue'
// import StepFour from '@/components/booking_flow/StepFour.vue'
import { useReservationStore } from '@/stores/reservation.store'
import BookingProgressBar from '@/components/booking_flow/BookingProgressBar.vue'

const reservationStore = useReservationStore()

defineProps<{ showModal: boolean }>()

const emit = defineEmits<{ (e: 'close'): void }>()

const closeModal = () => emit('close')

const step = ref(1)

const stepAnimation = ref('')

const route = useRoute()
const isModal = computed(() => !route.path.includes('/event-booking'))
const isMobile = ref(false)

onMounted(() => {
  isMobile.value = window.innerWidth < 768
})

const prevStep = () => {
  stepAnimation.value = ''
  step.value--
}

const nextStep = () => {
  if (step.value === 1 && reservationStore.checkStepOne()) {
    stepAnimation.value = 'step-fade'
    step.value++
  } else if (step.value === 2 && reservationStore.checkStepTwo()) {
    stepAnimation.value = 'step-fade'
    step.value++
  }
}

const goToStep = (index: number) => {
  if (index < step.value) {
    stepAnimation.value = ''
    step.value = index
  }
}
</script>

<style scoped lang="scss">
/* --- MODAL --- */
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
  max-width: 900px;
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

/* --- HEADER FULL WIDTH --- */
.booking-header {
  text-align: center;
  padding: 1.5rem 0;
  background: linear-gradient(70deg, rgba(255, 191, 0, 0.9), rgba(255, 255, 255, 0));
  width: 100%;
  border-radius: 8px 8px 0 0;
}

/* --- CONTENT --- */
/* Transition between steps */
.step-fade-enter-active,
.step-fade-leave-active {
  transition:
    opacity 0.4s ease,
    transform 0.4s ease;
}

.step-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.step-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* --- CLOSE BUTTON--- */
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
  flex-direction: column;
  gap: $spacing-m;
  align-items: center;

  .navigation {
    display: flex;
    gap: $spacing-m;
    flex: 1;
    width: 100%;
    .btn-submit {
      flex: 1;
    }
  }
}
</style>
