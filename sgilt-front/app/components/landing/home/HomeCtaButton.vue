<template>
  <NuxtLink to="/fete" class="home-cta">
    {{ $t('landing-page.cta') }}
  </NuxtLink>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-m $spacing-xl;
  border-radius: 9999px;
  background: $brand-accent;
  color: $brand-primary;
  font-size: $font-size-md;
  font-weight: $font-weight-bold;
  text-decoration: none;
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 28px rgba($brand-accent, 0.28);
  }

  &:focus-visible {
    outline: 3px solid $brand-primary;
    outline-offset: 4px;
  }
}
</style>
