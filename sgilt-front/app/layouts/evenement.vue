<template>
  <AppHeader />
  <EvenementBanner v-if="eventBannerVisible" />
  <main class="evenement-content" :class="{ 'has-banner': eventBannerVisible }">
    <slot />
  </main>
</template>

<script setup lang="ts">
import AppHeader from '~/components/AppHeader.vue'
import EvenementBanner from '~/components/evenement/EvenementBanner.vue'

const { visible: eventBannerVisible } = useEvenementBanner()
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

.evenement-content {
  padding-top: $app-header-height;
  flex: 1;
  display: flex;
  flex-direction: column;

  &.has-banner {
    @include below-banner;
  }
}
</style>
