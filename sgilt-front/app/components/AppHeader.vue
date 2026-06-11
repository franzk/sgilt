<template>
  <header class="app-header" :class="{ 'no-shadow': hideShadow }">
    <h1 class="logo" tabindex="0">
      <NuxtLink :to="logoLink">
        <img src="/sgilt-logo.svg" alt="SGILT" />
      </NuxtLink>
    </h1>

    <div v-if="showNotifications" class="quick-actions">
      <NotificationBell v-if="false" />

      <button
        ref="avatarRef"
        class="action-button"
        type="button"
        aria-label="Menu compte"
        @click="profileOpen = !profileOpen"
      >
        <UserAvatar :size="2.25" />
      </button>

      <ProfileMenuPopin :open="profileOpen" :anchor-el="avatarRef" @close="profileOpen = false" />
    </div>

    <button
      v-else-if="!isAuthenticated"
      class="action-button"
      type="button"
      aria-label="Se connecter"
      @click="handleLogin"
    >
      <UserIcon />
    </button>
  </header>
</template>

<script setup lang="ts">
import NotificationBell from '~/components/notifications/NotificationBell.vue'
import ProfileMenuPopin from '~/components/profile/ProfileMenuPopin.vue'
import UserAvatar from '~/components/basics/UserAvatar.vue'
import { UserIcon } from '@remixicons/vue/line'

defineProps<{
  showNotifications?: boolean
}>()

const avatarRef = ref<HTMLElement | null>(null)
const profileOpen = ref(false)

const { isAuthenticated, hasRole, login } = useKeycloak()

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

function handleLogin() {
  login({ redirectUri: window.location.origin + '/auth/redirect' })
}
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
