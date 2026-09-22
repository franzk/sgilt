<template>
  <div class="etape">
    <h2 class="question">{{ $t('tunnel.etape1.question') }}</h2>

    <DemandeOptionSelect
      :options="EVENT_TYPE_OPTIONS"
      :model-value="localEvent.eventType"
      :autre-value="localEvent.eventTypeAutre"
      autre-placeholder="Quel événement préparez-vous&nbsp;?"
      @update:model-value="localEvent.eventType = $event"
      @update:autre-value="localEvent.eventTypeAutre = $event"
      @change="next"
    />

    <DemandeCommentCaMarche class="flow-mobile" />
  </div>
</template>

<script setup lang="ts">
import { EVENT_TYPE_OPTIONS } from '~/types/demande'
import { useDemande } from '~/composables/useDemande'
import { useLocalEvent } from '~/composables/useLocalEvent'

const { next } = useDemande()
const { localEvent } = useLocalEvent()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.etape {
  .question {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.5rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0 0 $spacing-l;
    line-height: 1.3;
  }
}

.flow-mobile {
  margin-top: $spacing-l;

  @media (min-width: 640px) {
    display: none;
  }
}
</style>
