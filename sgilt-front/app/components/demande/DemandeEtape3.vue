<template>
  <div class="etape">
    <h2 class="etape__question">{{ $t('tunnel.etape3.question') }}</h2>

    <DemandeOptionSelect
      :options="MOMENT_CLE_OPTIONS"
      :model-value="localEvent.momentCle"
      :autre-value="localEvent.momentCleAutre"
      autre-placeholder="Décrivez le moment clé…"
      @update:model-value="localEvent.momentCle = $event"
      @update:autre-value="localEvent.momentCleAutre = $event"
      @change="next"
    />
  </div>
</template>

<script setup lang="ts">
import { MOMENT_CLE_OPTIONS } from '~/types/demande'
import { useDemande } from '~/composables/useDemande'
import { useLocalEvent } from '~/composables/useLocalEvent'

const { next } = useDemande()
const { localEvent } = useLocalEvent()
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
