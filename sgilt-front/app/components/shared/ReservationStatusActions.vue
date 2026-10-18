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
      {{ action.label }}
    </button>
  </div>
</template>

<script lang="ts">
// Un bouton : l'étape suivante en bouton plein ('main'), la sortie avant confirmation (annuler,
// refuser) en retrait ('exit').
export interface StatusAction {
  key: string
  label: string
  kind: 'main' | 'exit'
}
</script>

<script setup lang="ts">
// Boutons de statut d'une réservation, au-dessus de la frise de l'onglet Suivi, client comme
// prestataire. La page ouvre ses fenêtres de confirmation.
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
  flex-wrap: wrap;
  gap: $spacing-s;

  .status-btn {
    flex: 1;
    padding: $spacing-s $spacing-m;
    border-radius: 9999px;
    font-family: inherit;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    white-space: nowrap;
    cursor: pointer;
    transition:
      background 150ms ease,
      border-color 150ms ease;

    &.main {
      border: 1px solid $brand-accent;
      background: $brand-accent;
      color: $brand-primary;

      &:hover {
        filter: brightness(0.96);
      }
    }

    &.exit {
      flex: 0 1 auto;
      border: 1px solid rgba($state-error, 0.35);
      background: none;
      color: $state-error;

      &:hover {
        border-color: $state-error;
        background: rgba($state-error, 0.06);
      }
    }
  }
}
</style>
