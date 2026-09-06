<template>
  <NuxtLink to="/fete" class="home-cta" :class="{ compact }">
    {{ $t('landing-page.cta') }} <span aria-hidden="true">→</span>
  </NuxtLink>
</template>

<script setup lang="ts">
defineProps<{ compact?: boolean }>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.4em;
  padding: $spacing-m $spacing-xl;
  border-radius: 9999px;
  background: $brand-accent;
  color: $brand-primary;
  font-size: $font-size-md;
  font-weight: $font-weight-bold;
  text-decoration: none;
  white-space: nowrap;
  box-shadow: 0 4px 10px rgba($brand-primary, 0.18);
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba($brand-primary, 0.22);
  }

  &:focus-visible {
    outline: 3px solid $brand-primary;
    outline-offset: 4px;
  }

  &.compact {
    padding: $spacing-xs $spacing-m;
    font-size: $font-size-sm;
  }
}
</style>
