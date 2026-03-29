<template>
  <div class="etape">
    <h2 class="question">Votre événement en pratique</h2>
    <DemandeEtape5Fields :state="state" />
    <SgiltButton
      class="button"
      :disabled="!state.date || !state.ville || !state.nbInvites"
      @click="next"
      >Suivant</SgiltButton
    >
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import DemandeEtape5Fields from '~/components/demande/DemandeEtape5Fields.vue'
import { useDemande } from '~/composables/useDemande'

const { state, next } = useDemande()
</script>

<style scoped lang="scss">
.etape {
  .question {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.5rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0 0 $spacing-l;
    line-height: 1.3;
  }

  .button {
    margin-top: $spacing-l;
  }
}
</style>
