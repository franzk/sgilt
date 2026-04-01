<template>
  <header class="account-header">
    <NuxtLink to="/" class="account-header__logo">
      <img src="/sgilt-logo.svg" alt="SGILT" />
    </NuxtLink>
  </header>
  <main class="account-content">
    <slot />
  </main>
</template>

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
}

.account-content {
  padding-top: $app-header-height;
  flex: 1;
  display: flex;
  flex-direction: column;
}
</style>
