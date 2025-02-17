<template>
  <header>
    <!-- LOGO SGILT -->
    <h1 class="logo">
      <router-link :to="{ name: 'home' }">
        <img src="@/assets/images/sgilt.png" alt="SGILT" />
      </router-link>
    </h1>

    <!-- Quick Actions -->
    <div class="quick-actions" v-if="authStore.isConnected">
      <span class="action-button" @click="openNotifications"><IconBell /></span>
      <span class="action-button" @click="openProfile"><IconProfile /></span>
    </div>
  </header>
</template>

<script setup lang="ts">
import IconBell from '@/components/icons/IconBell.vue'
import IconProfile from '@/components/icons/IconProfile.vue'
import { useAuthStore } from '@/stores/auth.store'

const authStore = useAuthStore()

const openNotifications = () => {
  console.log('Ouverture des notifications...')
}

const openProfile = () => {
  console.log('Ouverture du profil...')
}
</script>

<style lang="scss" scoped>
header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: $z-app-header;
  background-color: $color-secondary;
  box-shadow: $box-shadow;
  padding: $spacing-mm $spacing-l $spacing-mm $spacing-mm;
}

/* LOGO SGILT */
.logo {
  margin: 0;
  img {
    height: 3rem;
  }
}

/* Quick Actions */
.quick-actions {
  display: flex;
  gap: $spacing-mm;

  .action-button {
    cursor: pointer;
    &:hover {
      opacity: 0.7;
    }
  }
}
</style>
