<template>
  <header class="app-header" :class="{ 'no-shadow': hideShadow }">
    <h1 class="logo" tabindex="0">
      <NuxtLink :to="logoLink">
        <img src="/sgilt-logo.svg" alt="SGILT" />
      </NuxtLink>
    </h1>

    <div class="quick-actions">
      <NotificationBell v-if="mounted && isAuthenticated" />

      <button
        ref="accountMenuAnchorRef"
        class="action-button"
        type="button"
        aria-label="Menu compte"
        @click="accountMenuOpen = !accountMenuOpen"
      >
        <UserAvatar v-if="mounted && isAuthenticated" :size="2.25" />
        <UserIcon v-else />
      </button>

      <AccountMenuPopin
        :open="accountMenuOpen"
        :anchor-el="accountMenuAnchorRef"
        @close="accountMenuOpen = false"
      />
    </div>
  </header>
</template>

<script setup lang="ts">
import AccountMenuPopin from '~/components/profile/AccountMenuPopin.vue'
import NotificationBell from '~/components/notifications/NotificationBell.vue'
import UserAvatar from '~/components/basics/UserAvatar.vue'
import { UserIcon } from '@remixicons/vue/line'

const accountMenuAnchorRef = ref<HTMLElement | null>(null)
const accountMenuOpen = ref(false)

const { isAuthenticated, hasRole } = useKeycloak()

const logoLink = computed(() => {
  if (!mounted.value || !isAuthenticated.value) return '/'
  return hasRole('PRO') ? '/pro' : '/app'
})

const ROUTES_WITHOUT_SHADOW_MOBILE = ['/', '/search']
const ROUTES_SHADOW_ON_SCROLL = ['/app', '/app/events', '/pro/reservations']
const route = useRoute()
const { isMobile } = useDevice()

const mounted = ref(false)
const scrolled = ref(false)

function updateScrolled() {
  scrolled.value = window.scrollY > 0
}

onMounted(() => {
  mounted.value = true
  window.addEventListener('scroll', updateScrolled, { passive: true })
  updateScrolled()
})

onUnmounted(() => {
  window.removeEventListener('scroll', updateScrolled)
})

const hideShadow = computed(() => {
  if (!mounted.value) return false
  if (isMobile.value && ROUTES_WITHOUT_SHADOW_MOBILE.includes(route.path)) return true
  if (isMobile.value && ROUTES_SHADOW_ON_SCROLL.includes(route.path)) return !scrolled.value
  return false
})
</script>

<style lang="scss" scoped>
@use '@/assets/styles/base' as *;

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: $app-header-height;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: $z-header;
  box-shadow: $box-shadow;
  background-color: #fff;
  padding: $spacing-xs $spacing-m;
  transition: box-shadow 200ms ease;

  &.no-shadow {
    box-shadow: none;
  }
}

.logo {
  margin: 0;

  img {
    height: 2rem;
    display: block;
  }
}

.quick-actions {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
}

.action-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.25rem;
  height: 2.25rem;
  color: $text-primary;
  background: none;
  cursor: pointer;
  text-decoration: none;
  transition: background 150ms ease;

  svg {
    width: 1.25rem;
    height: 1.25rem;
  }

  border: none;
  border-radius: 50%;
  overflow: hidden;
}
</style>
