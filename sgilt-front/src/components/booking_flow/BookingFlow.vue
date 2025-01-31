<template>
  <div class="booking-flow-modal" v-if="showModal" @click.self="closeModal">
    <div :class="['event-booking', { 'as-modal': isModal, 'full-height': isMobile }]">
      <div class="container">
        <!-- Bouton de fermeture uniquement si modale -->
        <button v-if="isModal" class="close-btn" @click="closeModal">✖</button>

        <header class="booking-header">
          <h2>{{ $t(`booking-flow.step-${step}.title`) }}</h2>
          <p>{{ $t(`booking-flow.step-${step}.subtitle`) }}</p>
        </header>

        <div class="content">
          <StepOne v-if="step === 1" />
          <StepTwo v-if="step === 2" />
        </div>

        <!-- Footer toujours visible -->
        <div class="modal-footer">
          <SgiltButton v-if="step === 2" @click="step--" variant="secondary">&larr;</SgiltButton>
          <SgiltButton class="btn-submit" @click="submit">{{
            step === 2 ? 'Envoyer ma réservation' : 'Continuer'
          }}</SgiltButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'
import StepOne from '@/components/booking_flow/StepOne.vue'
import StepTwo from '@/components/booking_flow/StepTwo.vue'
import { useReservationStore } from '@/stores/reservation.store'

const reservationStore = useReservationStore()

defineProps<{
  showModal: boolean
}>()

const emit = defineEmits<{ (e: 'close'): void }>()

const closeModal = () => emit('close')

const step = ref(1)

const route = useRoute()
const isModal = computed(() => !route.path.includes('/event-booking')) // Détermine si c'est une modale ou une page
const isMobile = ref(false)

onMounted(() => {
  isMobile.value = window.innerWidth < 768
})

const submit = () => {
  if (step.value === 1) {
    if (reservationStore.checkFirstValidation()) {
      step.value++
    }
  } else if (step.value === 2) {
    if (reservationStore.checkSecondValidation()) {
      step.value++
    }
  } else {
    // submit reservation
    closeModal()
    step.value = 1
  }
}
</script>

<style scoped lang="scss">
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
  z-index: $z-modal;
}

.event-booking {
  background: $color-white;
  padding: $spacing-l;
  border-radius: $border-radius-m;
  box-shadow: 0 8px 20px $shadow-m;
  width: 100%;
  max-width: 700px;
  margin: auto;
  position: relative;

  &.as-modal {
    background: white;
    padding: 2rem;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
    max-height: 80vh;
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

.container {
  display: flex;
  flex-direction: column;

  .content {
    flex: 1;
    padding: $spacing-m 0;
  }
}

.booking-header {
  text-align: center;
}

/* Bouton de fermeture */
.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

/* Footer sticky pour le CTA */
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
  }
}
</style>
