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
  position: sticky;
  z-index: 1;
  top: 0;
  left: 0;

  background-color: $color-secondary;
  box-shadow: 0 4px 6px $shadow;

  display: flex;
  flex: 1;
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
  }
}
</style>
