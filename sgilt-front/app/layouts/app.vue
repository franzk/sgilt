<template>
  <AppHeader />
  <main class="app-content">
    <slot />
  </main>
  <!-- Dans un événement, sa propre navigation remplace la navigation globale. -->
  <EventBottomNav v-if="eventId" :event-id="eventId" />
  <BottomNav v-else />
</template>

<script setup lang="ts">
import AppHeader from '~/components/AppHeader.vue'
import BottomNav from '~/components/app/BottomNav.vue'
import EventBottomNav from '~/components/app/EventBottomNav.vue'

useVirtualKeyboard()

const route = useRoute()
const eventId = computed(() =>
  typeof route.params.eventId === 'string' ? route.params.eventId : null,
)
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
