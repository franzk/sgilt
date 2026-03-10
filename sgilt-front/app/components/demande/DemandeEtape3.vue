<template>
  <div class="etape">
    <h2 class="etape__question">Quel serait le moment parfait de cette soirée&nbsp;?</h2>

    <DemandeOptionSelect
      :options="MOMENT_CLE_OPTIONS"
      :model-value="state.momentCle"
      :autre-value="state.momentCleAutre"
      autre-placeholder="Décrivez le moment clé…"
      @update:model-value="state.momentCle = $event"
      @update:autre-value="state.momentCleAutre = $event"
      @change="$emit('change')"
    />
  </div>
</template>

<script setup lang="ts">
import { MOMENT_CLE_OPTIONS } from '~/types/demande'
import type { DemandeState } from '~/types/demande'

defineProps<{ state: DemandeState }>()
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
</style>
