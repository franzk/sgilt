<template>
  <AppHeader :show-notifications="false" />
  <section class="default-content">
    <slot />
  </section>
</template>

<script setup lang="ts">
import AppHeader from '~/components/AppHeader.vue'
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

.default-content {
  padding-top: $app-header-height;
  flex: 1;
  display: flex;
  flex-direction: column;
}
</style>
