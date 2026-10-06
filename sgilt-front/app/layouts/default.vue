<template>
  <AppHeader />
  <ClientOnly>
    <ContextBanner v-if="showContextBanner" />
  </ClientOnly>
  <section class="default-content" :class="{ 'has-banner': showContextBanner }">
    <slot />
  </section>
</template>

<script setup lang="ts">
import AppHeader from '~/components/AppHeader.vue'
import ContextBanner from '~/components/app/ContextBanner.vue'

const { showContextBanner } = useFlow()
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

.default-content {
  --content-top: #{$app-header-height};
  padding-top: var(--content-top);
  flex: 1;
  display: flex;
  flex-direction: column;

  &.has-banner {
    --content-top: calc(#{$app-header-height} + 44px);
  }
}
</style>
