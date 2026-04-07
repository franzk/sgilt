<template>
  <AppHeader show-notifications />
  <main class="app-content">
    <slot />
  </main>
  <!--AppFooter /-->
</template>

<script setup lang="ts">
import AppHeader from '~/components/AppHeader.vue'
import AppFooter from '~/components/app/AppFooter.vue'
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

.app-content {
  padding-top: $app-header-height;
  padding-bottom: calc($bottom-nav-h + env(safe-area-inset-bottom, 0px));
  flex: 1;
  display: flex;
  flex-direction: column;

  @media (min-width: $breakpoint-desktop) {
    padding-bottom: 0;
  }
}
</style>
