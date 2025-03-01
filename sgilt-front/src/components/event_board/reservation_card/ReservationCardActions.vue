<template>
  <div class="reservation-actions">
    <div class="buttons">
      <!-- pay button -->
      <SgiltButton v-if="showPayButton" @click="$emit('pay')">Payer</SgiltButton>
      <!-- message button -->
      <SgiltButton @click="$emit('message')" variant="secondary">Message</SgiltButton>
    </div>
    <!-- cancel link -->
    <p v-if="canCancel" @click="$emit('cancel')" class="cancel">Annuler la réservation</p>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'

defineProps<{
  showPayButton: boolean
  canCancel: boolean
}>()

defineEmits(['pay', 'message', 'cancel'])
</script>

<style scoped lang="scss">
.reservation-actions {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  justify-content: center;

  .buttons {
    display: flex;
    gap: $spacing-s;
    > * {
      flex: 1;
    }
  }
}

.cancel {
  margin-top: $spacing-s;
  color: $color-error;
  cursor: pointer;
  text-decoration: underline;
}
</style>
