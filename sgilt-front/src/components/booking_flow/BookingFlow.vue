<template>
  <div class="booking-flow-modal" v-if="showModal" @click.self="closeModal">
    <div :class="['event-booking', { 'as-modal': isModal, 'full-height': isMobile }]">
      <div class="container">
        <!-- Bouton de fermeture uniquement si modale -->
        <button v-if="isModal" class="close-btn" @click="closeModal">✖</button>

        <header class="booking-header">
          <h2>📍 Détails de votre événement</h2>
          <p>Indiquez où et quand aura lieu votre événement.</p>
        </header>

        <SgiltFormGroup title="Lieu de l’événement">
          <input
            v-model="eventDetails.location"
            type="text"
            placeholder="Ex : Paris, Lyon, Marseille..."
          />
        </SgiltFormGroup>

        <div class="date-time">
          <SgiltFormGroup title="Date de l’événement">
            <SgiltDatePicker />
          </SgiltFormGroup>

          <SgiltFormGroup title="Heure de l’événement">
            <input type="time" />
          </SgiltFormGroup>
        </div>

        <SgiltFormGroup title="Envoyez un message à Marcel">
          <textarea v-model="eventDetails.message" placeholder="Ajoutez un message..."></textarea>
        </SgiltFormGroup>

        <!-- Footer toujours visible -->
        <div class="modal-footer">
          <SgiltButton @click="validateEvent">Continuer</SgiltButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'
import SgiltDatePicker from '@/components/basics/inputs/SgiltDatePicker.vue'
import SgiltFormGroup from '@/components/basics/inputs/SgiltFormGroup.vue'

defineProps<{
  showModal: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const closeModal = () => {
  emit('close')
}

const route = useRoute()
const isModal = computed(() => !route.path.includes('/event-booking')) // Détermine si c'est une modale ou une page
const isMobile = ref(false)

onMounted(() => {
  isMobile.value = window.innerWidth < 768
})

const eventDetails = ref({
  location: '',
  date: '',
  message: '',
})

const validateEvent = () => {
  if (!eventDetails.value.location || !eventDetails.value.date) {
    alert('Veuillez remplir tous les champs obligatoires.')
    return
  }
  console.log('Événement validé avec :', eventDetails.value)
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
    max-height: 90vh;
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

.booking-header {
  text-align: center;
}

.date-time {
  display: flex;
  gap: $spacing-m;
  & > * {
    flex: 1;
  }
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
  & > * {
    flex: 1;
  }
}
</style>
