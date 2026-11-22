<!--
  Navigation basse de l'espace client hors événement (mobile uniquement) : deux onglets,
  « Mes événements » (/app) et « À suivre » (/app/a-suivre). Dans un événement, EventBottomNav
  prend le relais (cf. layouts/app.vue).
-->
<template>
  <nav class="bottom-nav">
    <NuxtLink to="/app" class="item" :class="{ active: isEventsActive }">
      <Home2Icon class="icon" />
      <span class="label">{{ $t('nav.my-events') }}</span>
    </NuxtLink>

    <NuxtLink to="/app/a-suivre" class="item" :class="{ active: isToFollowActive }">
      <NotificationIcon class="icon" />
      <span class="label">{{ $t('nav.to-follow') }}</span>
    </NuxtLink>
  </nav>
</template>

<script setup lang="ts">
import { Home2Icon, NotificationIcon } from '@remixicons/vue/line'

const route = useRoute()
const isEventsActive = computed(() => route.path === '/app')
const isToFollowActive = computed(() => route.path === '/app/a-suivre')
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
