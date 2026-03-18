<template>
  <AppHeader :show-notifications="true" />
  <main class="app-content">
    <slot />
  </main>
  <AppFooter />
</template>

<script setup lang="ts">
import AppHeader from '~/components/AppHeader.vue'
import AppFooter from '~/components/app/AppFooter.vue'
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

$bottom-nav-h: 56px;

.app-content {
  padding-top: $app-header-height;
  padding-bottom: calc($bottom-nav-h + env(safe-area-inset-bottom, 0px));
  flex: 1;
  display: flex;
  flex-direction: column;

  @media (min-width: 900px) {
    padding-bottom: 0;
  }
}
</style>
