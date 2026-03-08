<template>
  <div class="etape">
    <h2 class="etape__question">Quel événement préparez-vous&nbsp;?</h2>

    <DemandeOptionSelect
      :options="EVENT_TYPE_OPTIONS"
      :model-value="state.eventType"
      :autre-value="state.eventTypeAutre"
      autre-placeholder="Décrivez votre événement…"
      @update:model-value="state.eventType = $event"
      @update:autre-value="state.eventTypeAutre = $event"
    />
  </div>
</template>

<script setup lang="ts">
import { EVENT_TYPE_OPTIONS } from '~/types/demande'
import type { DemandeState } from '~/types/demande'

defineProps<{ state: DemandeState }>()
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
