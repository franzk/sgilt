<template>
  <header class="app-header" :class="{ 'no-shadow': hideShadow }">
    <!-- LOGO SGILT -->
    <h1 class="logo">
      <NuxtLink to="/">
        <img src="/sgilt-logo.svg" alt="SGILT" />
      </NuxtLink>
    </h1>

    <!-- Quick Actions (plus tard / app) -->
    <div v-if="isConnected" class="quick-actions">
      <button
        class="action-button"
        type="button"
        @click="openNotifications"
        aria-label="Notifications"
      >
        <!--IconBell /-->
      </button>
      <button class="action-button" type="button" @click="openProfile" aria-label="Profil">
        <!--IconProfile /-->
      </button>
    </div>
  </header>
</template>

<script setup lang="ts">
/**
 * Pour l'instant (focus public), on mock.
 * Plus tard tu branches ton Pinia authStore (Keycloak, etc.)
 */
const isConnected = false

const openNotifications = () => {
  console.log('Ouverture des notifications...')
}

const openProfile = () => {
  console.log('Ouverture du profil...')
}

// Le shadow est masqué sur mobile pour les pages où le contenu
// doit visuellement se fondre avec le header (index, search)
const ROUTES_WITHOUT_SHADOW_MOBILE = ['/', '/search']

const route = useRoute()
const { isMobile } = useDevice()

// On n'applique la classe qu'après le montage côté client
// pour éviter un hydration mismatch SSR/client
const mounted = ref(false)
onMounted(() => {
  mounted.value = true
})

const hideShadow = computed(() => {
  return mounted.value && isMobile.value && ROUTES_WITHOUT_SHADOW_MOBILE.includes(route.path)
})
</script>

<style lang="scss" scoped>
@use '@/assets/styles/base' as *;
@use '@/assets/styles/borders' as *;

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: $app-header-height;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 50;
  box-shadow: $box-shadow;
  background-color: #fff;
  padding: $spacing-xs;
  transition: box-shadow 200ms ease;

  &.no-shadow {
    box-shadow: none;
  }
}

/* LOGO SGILT */
.logo {
  margin: 0;
  img {
    height: 2rem;
  }
}

/* Quick Actions */
.quick-actions {
  display: flex;
  gap: $spacing-s;

  .action-button {
    cursor: pointer;
    padding: $spacing-xs;
    border: 0;
    border-radius: $radius-md;
    background: transparent;
    line-height: 0;

    &:hover {
      opacity: 0.7;
    }
  }
}
</style>
