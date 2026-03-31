<template>
  <header class="app-header" :class="{ 'no-shadow': hideShadow }">
    <h1 class="logo">
      <NuxtLink :to="showNotifications ? '/app/events' : '/'">
        <img src="/sgilt-logo.svg" alt="SGILT" />
      </NuxtLink>
    </h1>

    <div v-if="showNotifications" class="quick-actions">
      <NuxtLink to="/app/notifications" class="action-button" aria-label="Notifications">
        <span class="bell-wrap">
          <IconBell />
          <!-- badge point : à brancher sur le store notifs -->
          <!-- <span class="bell-badge" /> -->
        </span>
      </NuxtLink>

      <NuxtLink to="/app/profile" class="action-button" aria-label="Profil">
        <div class="avatar">
          <!-- avatar photo : <img v-if="user.photo" ... /> -->
          <span class="avatar__initials">JT</span>
        </div>
      </NuxtLink>
    </div>
  </header>
</template>

<script setup lang="ts">
import IconBell from '~/components/icons/IconBell.vue'

const props = defineProps<{ showNotifications?: boolean }>()

const ROUTES_WITHOUT_SHADOW_MOBILE = ['/', '/search']
const route = useRoute()
const { isMobile } = useDevice()

const mounted = ref(false)
onMounted(() => {
  mounted.value = true
})

const hideShadow = computed(
  () => mounted.value && isMobile.value && ROUTES_WITHOUT_SHADOW_MOBILE.includes(route.path),
)
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
  border-radius: 50%;
  color: $text-primary;
  text-decoration: none;
  transition: background 150ms ease;

  &:hover {
    background: $surface-soft;
  }
}

.bell-wrap {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.bell-badge {
  position: absolute;
  top: -1px;
  right: -1px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: $brand-accent;
  border: 1.5px solid #fff;
}

.avatar {
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  background: $brand-subtle;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  &__initials {
    font-family: 'Cormorant Garamond', serif;
    font-size: 0.85rem;
    font-weight: 500;
    color: $text-primary;
    line-height: 1;
  }
}
</style>
