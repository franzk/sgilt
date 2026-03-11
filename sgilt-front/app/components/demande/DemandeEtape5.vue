<template>
  <div class="etape">
    <h2 class="etape__question">Votre événement en pratique</h2>

    <DemandeEtape5Fields :state="state" mobile />

    <DemandeRecap
      :event-type-label="eventTypeLabel"
      :event-type-emoji="eventTypeEmoji"
      :ambiance-label="ambianceLabel"
      :ambiance-emoji="ambianceEmoji"
      :moment-cle-label="momentCleLabel"
      :moment-cle-emoji="momentCleEmoji"
      :date="state.date"
      :ville="state.ville"
      :nb-invites="state.nbInvites"
      :lieu="state.lieu"
    />

    <div class="etape__cta">
      <SgiltButton :disabled="!formValid" @click="$emit('change')">Continuer →</SgiltButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import type { DemandeState } from '~/types/demande'

const props = defineProps<{
  state: DemandeState
  eventTypeLabel: string | null
  eventTypeEmoji: string
  ambianceLabel: string | null
  ambianceEmoji: string
  momentCleLabel: string | null
  momentCleEmoji: string
}>()

defineEmits<{ (e: 'change'): void }>()

const formValid = computed(
  () =>
    !!props.state.date &&
    !!props.state.ville.trim() &&
    !!props.state.nbInvites.trim() &&
    Number(props.state.nbInvites) > 0,
)
</script>

<style scoped lang="scss">
.etape {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;

  &__question {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.5rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0;
    line-height: 1.3;
  }

  &__cta {
    button {
      width: 100%;
      height: 3rem;
    }
  }
}
</style>
