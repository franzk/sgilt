<template>
  <nav class="pro-footer">
    <NuxtLink
      to="/pro/reservations"
      class="pro-footer__item"
      :class="{ 'pro-footer__item--active': route.path.startsWith('/pro/reservations') }"
    >
      <IconEvent class="pro-footer__icon" />
      <span class="pro-footer__label">Réservations</span>
    </NuxtLink>

    <NuxtLink
      to="/pro/notifications"
      class="pro-footer__item"
      :class="{ 'pro-footer__item--active': route.path.startsWith('/pro/notifications') }"
    >
      <IconBell class="pro-footer__icon" />
      <span class="pro-footer__label">Notifications</span>
    </NuxtLink>

    <NuxtLink
      to="/pro/profil"
      class="pro-footer__item"
      :class="{ 'pro-footer__item--active': route.path.startsWith('/pro/profil') }"
    >
      <IconProfile class="pro-footer__icon" />
      <span class="pro-footer__label">Profil</span>
    </NuxtLink>
  </nav>
</template>

<script setup lang="ts">
import IconEvent from '~/components/icons/IconEvent.vue'
import IconBell from '~/components/icons/IconBell.vue'
import IconProfile from '~/components/icons/IconProfile.vue'

const route = useRoute()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;
$nav-h: 56px;

.pro-footer {
  @media (min-width: $breakpoint-desktop) {
    display: none !important;
  }

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

  &__item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 3px;
    color: $brand-muted;
    text-decoration: none;
    transition: color 150ms ease;

    &--active {
      color: $brand-accent;
    }
  }

  &__icon {
    width: 22px;
    height: 22px;
  }

  &__label {
    font-size: 0.65rem;
    font-weight: 500;
    letter-spacing: 0.02em;
  }
}
</style>
