<template>
  <button class="critical-actions-link" type="button" @click="navigateTo(`/pro/reservations/${demandeId}/actions`)">
    Actions spéciales
  </button>
</template>

<script setup lang="ts">
defineProps<{ demandeId: string }>()
</script>

<style scoped lang="scss">
.critical-actions-link {
  display: inline-block;
  padding: $spacing-s 0;
  border: none;
  background: none;
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 500;
  color: $text-secondary;
  text-align: left;
  cursor: pointer;
  transition: color 150ms ease;

  &:hover {
    color: $text-primary;
  }
}
</style>
