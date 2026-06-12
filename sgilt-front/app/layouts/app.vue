<template>
  <AppHeader />
  <main class="app-content">
    <slot />
  </main>
  <BottomNav />
</template>

<script setup lang="ts">
import AppHeader from '~/components/AppHeader.vue'
import BottomNav from '~/components/app/BottomNav.vue'

useVirtualKeyboard()
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
