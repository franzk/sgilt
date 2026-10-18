<template>
  <NuxtLink :to="to" class="special-actions-link">
    {{ $t('reservation.special-actions.link') }}
  </NuxtLink>
</template>

<script setup lang="ts">
// Lien discret vers la page « Actions spéciales » d'une réservation confirmée (annulation après
// confirmation), en bas de l'onglet Suivi, client comme prestataire. Jamais mis en avant.
defineProps<{ to: string }>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.special-actions-link {
  align-self: flex-start;
  padding: $spacing-s 0;
  color: $text-secondary;
  font-size: 0.8rem;
  font-weight: $font-weight-medium;
  text-decoration: none;
  transition: color 150ms ease;

  &:hover {
    color: $text-primary;
  }
}
</style>
