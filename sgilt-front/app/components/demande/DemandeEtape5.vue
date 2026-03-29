<template>
  <div class="etape">
    <h2 class="question">Votre événement en pratique</h2>

    <DemandeEtape5Fields :state="state" mobile />

    <DemandeRecap />

    <div class="cta">
      <SgiltButton :disabled="!formValid" @click="next">Continuer →</SgiltButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { useDemande } from '~/composables/useDemande'

const {
  state,
  next,
  eventTypeLabel,
  eventTypeEmoji,
  ambianceLabel,
  ambianceEmoji,
  momentCleLabel,
  momentCleEmoji,
} = useDemande()

const formValid = computed(
  () =>
    !!state.date && !!state.ville.trim() && !!state.nbInvites.trim() && Number(state.nbInvites) > 0,
)
</script>

<style scoped lang="scss">
.etape {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;

  .question {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.5rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0;
    line-height: 1.3;
  }

  .cta {
    button {
      width: 100%;
      height: 3rem;
    }
  }
}
</style>
