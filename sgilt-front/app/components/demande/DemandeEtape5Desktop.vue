<template>
  <div class="etape">
    <h2 class="etape__question">Votre événement en pratique</h2>
    <DemandeEtape5Fields :state="state" />
    <SgiltButton
      class="etape__button"
      :disabled="!state.date || !state.ville || !state.nbInvites"
      @click="$emit('change')"
      >Suivant</SgiltButton
    >
  </div>
</template>

<script setup lang="ts">
import type { DemandeState } from '~/types/demande'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import DemandeEtape5Fields from '~/components/demande/DemandeEtape5Fields.vue'

defineProps<{
  state: DemandeState
}>()

const emit = defineEmits<{
  (e: 'change'): void
}>()
</script>

<style scoped lang="scss">
.etape {
  &__question {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.5rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0 0 $spacing-l;
    line-height: 1.3;
  }
}

.etape__button {
  height: 3rem;
  width: 100%;
  margin-top: $spacing-l;
}
</style>
