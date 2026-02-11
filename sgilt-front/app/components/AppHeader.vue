<template>
  <header class="app-header">
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
 * Pour l’instant (focus public), on mock.
 * Plus tard tu branches ton Pinia authStore (Keycloak, etc.)
 */
const isConnected = false

const openNotifications = () => {
  console.log('Ouverture des notifications...')
}
const openProfile = () => {
  console.log('Ouverture du profil...')
}
</script>

<style lang="scss" scoped>
.app-header {
  display: flex;
  height: 64px; // remplace par ta var $app-header-height quand tu remets tes tokens
  align-items: center;
  justify-content: space-between;

  position: sticky; // mieux que fixed pour éviter les soucis de layout
  top: 0;
  z-index: 50;

  background-color: rgba(255, 255, 255, 0.8); // tu pourras remettre $color-secondary
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);

  padding: 10px 16px;
}

/* LOGO SGILT */
.logo {
  margin: 0;

  img {
    height: 3rem;
    display: block;
  }
}

/* Quick Actions */
.quick-actions {
  display: flex;
  gap: 10px;

  .action-button {
    cursor: pointer;
    padding: 8px;
    border: 0;
    border-radius: 10px;
    background: transparent;
    line-height: 0;

    &:hover {
      opacity: 0.7;
    }
  }
}
</style>
