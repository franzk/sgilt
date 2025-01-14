<template>
  <div class="error-not-found">
    <h1>{{ $t('errors.404.title') }}</h1>
    <p>{{ $t('errors.404.message') }}</p>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
.error-not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  gap: 1rem;
}
</style>
