<template>
  <AppHeader />
  <ClientOnly>
    <ContextBanner v-if="showContextBanner" />
  </ClientOnly>
  <EvenementBanner v-if="eventBannerVisible" />
  <section class="default-content" :class="{ 'has-banner': hasBanner }">
    <slot />
  </section>
</template>

<script setup lang="ts">
import AppHeader from '~/components/AppHeader.vue'
import ContextBanner from '~/components/app/ContextBanner.vue'
import EvenementBanner from '~/components/evenement/EvenementBanner.vue'

const { showContextBanner } = useFlow()
const { visible: eventBannerVisible } = useEvenementBanner()
const hasBanner = computed(() => showContextBanner.value || eventBannerVisible.value)
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

.default-content {
  padding-top: $app-header-height;
  flex: 1;
  display: flex;
  flex-direction: column;

  // below-banner expose aussi --banner-offset (éléments sticky : voir PrestataireDetails).
  &.has-banner {
    @include below-banner;
  }
}
</style>
