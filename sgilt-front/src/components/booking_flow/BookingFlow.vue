<template>
  <div class="booking-flow-modal" v-if="showModal" @mousedown.self="closeModal">
    <div
      class="booking-flow as-modal"
      :class="{ mobile: mobileView, tablet: tabletView }"
      ref="modal"
    >
      <div class="container">
        <!-- Close button-->
        <button class="close-btn" @click="closeModal">✖</button>

        <!-- Header -->
        <header class="booking-header">
          <h2>{{ $t(`booking-flow.step-${step}.title`) }}</h2>
          <p>{{ $t(`booking-flow.step-${step}.subtitle`) }}</p>
          <BookingProgressBar v-model="step" @stepChange="goToStep" />
        </header>

        <!-- Dynamic content -->
        <transition :name="stepAnimation" mode="out-in">
          <div :class="{ 'v-center': step == 2 }" :key="step" class="content">
            <StepOne v-if="step === 1" />
            <StepTwo v-if="step === 2" />
            <StepThree v-if="step === 3" />
            <StepFour v-if="step === 4" @close="closeModal" />
          </div>
        </transition>

        <div class="tablet-illustration" v-if="tabletView && step < 3">
          <BookingFlowIllustration />
        </div>

        <!-- Sticky Footer -->
        <div class="modal-footer">
          <!-- Navigation buttons : step 1, 2, 3 -->
          <div class="navigation">
            <SgiltButton v-if="[2, 3].includes(step)" @click="prevStep" variant="secondary">{{
              $t('booking-flow.back-button')
            }}</SgiltButton>
            <SgiltButton class="btn-submit" @click="nextStep" v-if="step < 4">
              {{ $t(`booking-flow.step-${step}.cta`) }}
            </SgiltButton>
            <SgiltButton v-if="step === 4">
              {{ $t('booking-flow.step-4.cta.event-board') }}
            </SgiltButton>
            <SgiltButton v-if="step === 4" @click="closeModal" variant="secondary">
              {{ $t('booking-flow.step-4.cta.close') }}
            </SgiltButton>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'
import StepOne from '@/components/booking_flow/StepOne.vue'
import StepTwo from '@/components/booking_flow/StepTwo.vue'
import StepThree from '@/components/booking_flow/StepThree.vue'
import StepFour from '@/components/booking_flow/StepFour.vue'
import { useReservationStore } from '@/stores/reservation.store'
import { mobileView, tabletView } from '@/utils/StyleUtils'
import BookingProgressBar from '@/components/booking_flow/BookingProgressBar.vue'
import BookingFlowIllustration from '@/components/booking_flow/BookingFlowIllustration.vue'

const reservationStore = useReservationStore()

defineProps<{ showModal: boolean }>()

const emit = defineEmits<{ (e: 'close'): void }>()

const closeModal = () => emit('close')

const step = ref(1)

const stepAnimation = ref('')

const prevStep = () => {
  stepAnimation.value = ''
  step.value--
}

const modal = ref<HTMLElement | null>(null)
const nextStep = () => {
  if (
    (step.value === 1 && reservationStore.checkStepOne()) ||
    (step.value === 2 && reservationStore.checkStepTwo())
  ) {
    stepAnimation.value = 'step-fade'
    step.value++
    modal.value?.scrollTo({ top: 0, behavior: 'auto' })
  } else if (step.value === 3) {
    step.value++
    modal.value?.scrollTo({ top: 0, behavior: 'auto' })
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
  background: $booking-modal-overlay;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: $z-modal;

  .booking-flow {
    background: $color-white;
    border-radius: $border-radius-m;
    width: 100%;
    max-width: 900px;
    margin: auto;
    position: relative;

    &.as-modal {
      box-shadow: $modal-box-shadow;
      height: 90vh;
      max-height: 800px;
      overflow-y: auto;
    }

    &.mobile {
      top: 0;
      left: 0;
      width: 100%;
      height: 80%;
      background: white;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }

    &.tablet {
      width: 80%;
      max-width: 800px;
      height: 70%;
      max-height: 1000px;
    }

    .container {
      height: 100%;
      position: relative;
      display: flex;
      flex-direction: column;
    }

    // Header
    .booking-header {
      text-align: center;
      padding: $spacing-ml 0;
      background: linear-gradient(70deg, $color-accent, rgba(255, 255, 255, 0));
      width: 100%;
      border-radius: $border-radius-s $border-radius-s 0 0;
    }

    // modal content
    .content {
      padding: $spacing-m;
      flex: 1;
      display: flex;
      flex-direction: column;
    }

    .v-center {
      @include respond-to(desktop) {
        justify-content: center;
      }
    }

    // tablet illustration
    .tablet-illustration {
      flex: 1;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    // footer
    .modal-footer {
      position: sticky;
      bottom: 0;
      background: white;
      padding: $spacing-m;
      border-top: 1px solid $shadow-s;
      display: flex;
      flex-direction: column;
      gap: $spacing-m;
      align-items: center;

      .navigation {
        display: flex;
        gap: $spacing-m;
        justify-content: center;
        flex: 1;
        width: 100%;
        .btn-submit {
          flex: 1;
        }
      }

      .step-4-cta {
        display: flex;
        flex-direction: column;
        gap: $spacing-s;
        align-items: center;
        text-align: center;

        .step-4-buttons {
          display: flex;
          gap: $spacing-m;
        }

        .small-text {
          font-size: 0.9rem;
          color: $color-subtext;
          font-style: italic;
        }
      }
    }
  }
}

/* animation : transition between steps */
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

// close button
.close-btn {
  position: absolute;
  top: $spacing-s;
  right: $spacing-s;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}
</style>
