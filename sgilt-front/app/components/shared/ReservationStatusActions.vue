<!--
  Boutons de statut d'une réservation, dans l'onglet Suivi, client comme prestataire : l'étape
  suivante en bouton plein ('main'), la sortie avant confirmation (annuler, refuser) en retrait
  ('exit'). Chaque bouton a son icône. Sur mobile, un bouton par ligne en pleine largeur ; sur
  desktop, côte à côte. La page qui les affiche ouvre ses fenêtres de confirmation.
-->
<template>
  <div v-if="actions.length > 0" class="status-actions">
    <button
      v-for="action in actions"
      :key="action.key"
      class="status-btn"
      :class="action.kind"
      type="button"
      @click="$emit('action', action.key)"
    >
      <component :is="action.icon" class="icon" aria-hidden="true" />
      {{ action.label }}
    </button>
  </div>
</template>

<script lang="ts">
import type { Component } from 'vue'

// Un bouton : l'étape suivante en bouton plein ('main'), la sortie avant confirmation en retrait
// ('exit').
export interface StatusAction {
  key: string
  label: string
  icon: Component
  kind: 'main' | 'exit'
}
</script>

<script setup lang="ts">
defineProps<{
  actions: StatusAction[]
}>()

defineEmits<{
  action: [key: string]
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.status-actions {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  @media (min-width: $breakpoint-desktop) {
    flex-direction: row;
    flex-wrap: wrap;
    gap: $spacing-s;
  }

  .status-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-xs;
    width: 100%;
    padding: $spacing-xs $spacing-m;
    border-radius: 9999px;
    font-family: inherit;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    cursor: pointer;
    transition:
      background 150ms ease,
      border-color 150ms ease;

    @media (min-width: $breakpoint-desktop) {
      flex: 1;
      width: auto;
      white-space: nowrap;
    }

    .icon {
      flex-shrink: 0;
      width: 1.125rem;
      height: 1.125rem;
    }

    &.main {
      border: 1px solid $brand-accent;
      background: $brand-accent;
      color: $brand-primary;

      &:hover {
        filter: brightness(0.96);
      }
    }

    &.exit {
      border: 1px solid rgba($state-error, 0.35);
      background: none;
      color: $state-error;

      @media (min-width: $breakpoint-desktop) {
        flex: 0 1 auto;
      }

      &:hover {
        border-color: $state-error;
        background: rgba($state-error, 0.06);
      }
    }
  }
}
</style>
