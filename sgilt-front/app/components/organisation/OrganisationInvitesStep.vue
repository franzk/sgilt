<template>
  <div class="invites-step">
    <button
      v-for="tranche in NB_INVITES_OPTIONS"
      :key="tranche"
      class="tranche"
      :class="{ selected: localEvent.nbInvites === tranche }"
      type="button"
      :aria-pressed="localEvent.nbInvites === tranche"
      @click="toggle(tranche)"
    >
      {{ tranche }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { NB_INVITES_OPTIONS } from '~/types/demande'
import { useLocalEvent } from '~/composables/useLocalEvent'

const { localEvent } = useLocalEvent()

// Re-cliquer la tranche déjà choisie la désélectionne.
function toggle(tranche: string) {
  localEvent.nbInvites = localEvent.nbInvites === tranche ? '' : tranche
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.invites-step {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-s;

  .tranche {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 4.25rem;
    padding: $spacing-xs;
    border: 1.5px solid $divider-color;
    border-radius: $radius-lg;
    background: $surface-white;
    color: $text-primary;
    font-family: inherit;
    font-size: $font-size-sm;
    text-align: center;
    cursor: pointer;
    transition:
      border-color 180ms ease,
      background 180ms ease;

    &:hover {
      border-color: rgba($brand-accent, 0.7);
    }

    &.selected {
      border-color: $brand-accent;
      background: rgba($brand-accent, 0.14);
      font-weight: $font-weight-semibold;
    }
  }
}
</style>
