<template>
  <header>
    <h1>
      <router-link :to="{ name: 'home' }">{{ $t('app-header.app-name') }}</router-link>
    </h1>
    <p>{{ $t('app-header.slogan') }}</p>
  </header>
</template>

<script setup lang="ts"></script>

<style lang="scss" scoped>
header {
  flex: 0 0 2rem;

  display: flex;
  flex-direction: row;

  position: sticky;
  top: 0;
  left: 0;
  z-index: 1;

  background-color: $color-secondary;
  box-shadow: 0 4px 6px $shadow-m;

  gap: $spacing-l;
  align-content: center;
  flex-wrap: wrap;

  padding-left: 1rem;

  h1 a {
    text-decoration: none;
    color: $color-primary;
  }

  p {
    padding: 0;
    margin: 0;
    align-content: inherit;
    @include respond-to(mobile) {
      display: none;
    }
  }
}
</style>
