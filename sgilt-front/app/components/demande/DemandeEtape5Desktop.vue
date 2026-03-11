<template>
  <div class="etape">
    <h2 class="etape__question">Votre événement en pratique</h2>
    <DemandeEtape5Fields :state="state" />
  </div>
</template>

<script setup lang="ts">
import type { DemandeState } from '~/types/demande'

defineProps<{
  state: DemandeState
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
</style>
