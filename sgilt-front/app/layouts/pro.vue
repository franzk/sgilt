<template>
  <AppHeader :show-notifications="true" />
  <main class="pro-content">
    <slot />
  </main>
  <ProFooter />
</template>

<script setup lang="ts">
import AppHeader from '~/components/AppHeader.vue'
import ProFooter from '~/components/pro/ProFooter.vue'
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

$bottom-nav-h: 56px;

.pro-content {
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
