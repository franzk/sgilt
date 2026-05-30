<template>
  <nav class="bottom-nav">
    <NuxtLink to="/app" class="item" :class="{ active: isHomeActive }">
      <Home2Icon class="icon" />
      <span class="label">{{ $t('nav.home') }}</span>
    </NuxtLink>

    <NuxtLink to="/app/events" class="item" :class="{ active: isEventsActive }">
      <CalendarEventIcon class="icon" />
      <span class="label">{{ $t('nav.events') }}</span>
    </NuxtLink>
  </nav>
</template>

<script setup lang="ts">
import { CalendarEventIcon, Home2Icon } from '@remixicons/vue/line'

const route = useRoute()
const isHomeActive = computed(() => route.path === '/app')
const isEventsActive = computed(() => route.path.startsWith('/app/events'))
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$nav-h: $bottom-nav-h;

.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: $z-header;
  height: calc($nav-h + env(safe-area-inset-bottom, 0px));
  padding-bottom: env(safe-area-inset-bottom, 0px);
  display: flex;
  align-items: stretch;
  background: #fff;
  border-top: 1px solid $divider-color;

  @media (min-width: $breakpoint-desktop) {
    display: none;
  }

  .item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 3px;
    color: $brand-muted;
    transition: color 150ms ease;
    text-decoration: none;

    &.active {
      color: $brand-accent;
    }
  }

  .icon {
    width: 22px;
    height: 22px;
  }

  .label {
    font-size: 0.65rem;
    font-weight: 500;
    letter-spacing: 0.02em;
  }
}
</style>
