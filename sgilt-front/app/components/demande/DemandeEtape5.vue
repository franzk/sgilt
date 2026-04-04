<template>
  <div class="etape">
    <h2 class="question">{{ $t('tunnel.etape5.question') }}</h2>
    <DemandeEtape5Fields :state="state" mobile />
    <DemandeRecap />
  </div>
</template>

<script setup lang="ts">
import { useDemande } from '~/composables/useDemande'

const { state } = useDemande()
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
}
</style>
