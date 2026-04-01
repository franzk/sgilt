<template>
  <AppHeader show-notifications />
  <main class="account-content">
    <slot />
  </main>
</template>

<script setup lang="ts">
import AppHeader from '~/components/AppHeader.vue'

const avatarRef = ref<HTMLElement | null>(null)
const profileOpen = ref(false)
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

.account-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: $z-header;
  height: $app-header-height;
  display: flex;
  align-items: center;
  padding: 0 $spacing-m;
  background: #fff;
  border-bottom: 1px solid $divider-color;

  &__logo img {
    height: 2rem;
    display: block;
  }

  &__avatar {
    background: none;
    border: none;
    padding: 0;
    cursor: pointer;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: opacity 150ms ease;

    &:hover {
      opacity: 0.8;
    }
  }
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

.account-content {
  padding-top: $app-header-height;
  flex: 1;
  display: flex;
  flex-direction: column;
}
</style>
