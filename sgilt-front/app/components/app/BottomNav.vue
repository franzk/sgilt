<template>
  <nav class="bottom-nav">
    <NuxtLink
      to="/app/events"
      class="bottom-nav__item"
      :class="{ 'bottom-nav__item--active': isEventsActive }"
    >
      <IconEvent class="bottom-nav__icon" />
      <span class="bottom-nav__label">Événements</span>
    </NuxtLink>

    <NuxtLink
      to="/app/notifications"
      class="bottom-nav__item"
      :class="{ 'bottom-nav__item--active': route.path.startsWith('/app/notifications') }"
    >
      <IconBell class="bottom-nav__icon" />
      <span class="bottom-nav__label">Notifications</span>
    </NuxtLink>

    <NuxtLink
      to="/app/profile"
      class="bottom-nav__item"
      :class="{ 'bottom-nav__item--active': route.path.startsWith('/app/profile') }"
    >
      <IconProfile class="bottom-nav__icon" />
      <span class="bottom-nav__label">Profil</span>
    </NuxtLink>
  </nav>
</template>

<script setup lang="ts">
import IconEvent from '~/components/icons/IconEvent.vue'
import IconBell from '~/components/icons/IconBell.vue'
import IconProfile from '~/components/icons/IconProfile.vue'

const route = useRoute()
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

  &__item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 3px;
    color: $brand-muted;
    transition: color 150ms ease;
    text-decoration: none;

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
