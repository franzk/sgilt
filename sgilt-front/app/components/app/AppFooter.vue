<template>
  <nav class="app-footer">
    <NuxtLink
      to="/app/events"
      class="app-footer__item"
      :class="{ 'app-footer__item--active': isEventsActive }"
    >
      <IconEvent class="app-footer__icon" />
      <span class="app-footer__label">Événements</span>
    </NuxtLink>

    <NuxtLink
      to="/app/notifications"
      class="app-footer__item"
      :class="{ 'app-footer__item--active': route.path.startsWith('/app/notifications') }"
    >
      <IconBell class="app-footer__icon" />
      <span class="app-footer__label">Notifications</span>
    </NuxtLink>

    <NuxtLink
      to="/app/profile"
      class="app-footer__item"
      :class="{ 'app-footer__item--active': route.path.startsWith('/app/profile') }"
    >
      <IconProfile class="app-footer__icon" />
      <span class="app-footer__label">Profil</span>
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
$nav-h: 56px;

.app-footer {
  @media (min-width: 900px) {
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
