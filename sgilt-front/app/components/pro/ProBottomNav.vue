<!--
  Navigation basse de l'espace prestataire (mobile uniquement) : quatre onglets, « Accueil » (/pro),
  « Réservations » (/pro/reservations), « Calendrier » (/pro/calendrier) et « Ma page »
  (/pro/ma-page, récap qui ouvre l'édition plein écran). Notifications et profil passent par la cloche et l'avatar d'AppHeader.
-->
<template>
  <nav class="bottom-nav">
    <NuxtLink to="/pro" class="item" :class="{ active: isHomeActive }">
      <Home2Icon class="icon" />
      <span class="label">{{ $t('nav.home') }}</span>
    </NuxtLink>

    <NuxtLink to="/pro/reservations" class="item" :class="{ active: isReservationsActive }">
      <FileList3Icon class="icon" />
      <span class="label">{{ $t('nav.reservations') }}</span>
    </NuxtLink>

    <NuxtLink to="/pro/calendrier" class="item" :class="{ active: isCalendarActive }">
      <CalendarCheckIcon class="icon" />
      <span class="label">{{ $t('nav.calendar') }}</span>
    </NuxtLink>

    <NuxtLink to="/pro/ma-page" class="item" :class="{ active: isMyPageActive }">
      <Store2Icon class="icon" />
      <span class="label">{{ $t('nav.my-page') }}</span>
    </NuxtLink>
  </nav>
</template>

<script setup lang="ts">
import { Home2Icon, FileList3Icon, CalendarCheckIcon, Store2Icon } from '@remixicons/vue/line'

const route = useRoute()
const isHomeActive = computed(() => route.path === '/pro')
const isReservationsActive = computed(() => route.path.startsWith('/pro/reservations'))
const isCalendarActive = computed(() => route.path === '/pro/calendrier')
const isMyPageActive = computed(() => route.path === '/pro/ma-page')
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
  background: $surface-white;
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
    gap: 0.1875rem;
    color: $brand-muted;
    transition: color 150ms ease;
    text-decoration: none;

    &.active {
      color: $brand-accent;
    }
  }

  .icon {
    width: 1.375rem;
    height: 1.375rem;
  }

  .label {
    font-size: 0.65rem;
    font-weight: 500;
    letter-spacing: 0.02em;
  }
}
</style>
