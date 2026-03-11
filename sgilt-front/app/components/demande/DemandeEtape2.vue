<template>
  <div class="etape">
    <h2 class="etape__question">Quelle ambiance souhaitez-vous créer&nbsp;?</h2>

    <DemandeOptionSelect
      :options="AMBIANCE_OPTIONS"
      :model-value="state.ambiance"
      :autre-value="state.ambianceAutre"
      autre-placeholder="Décrivez l'ambiance souhaitée…"
      @update:model-value="state.ambiance = $event"
      @update:autre-value="state.ambianceAutre = $event"
      @change="next"
    />
  </div>
</template>

<script setup lang="ts">
import { AMBIANCE_OPTIONS } from '~/types/demande'
import { useDemande } from '~/composables/useDemande'

const { state, next } = useDemande()
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
</style>
