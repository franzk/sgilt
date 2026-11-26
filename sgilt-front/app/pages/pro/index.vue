<!--
  Accueil de l'espace prestataire (/pro).
-->
<template>
  <div class="pro-home" />
</template>

<script setup lang="ts">
definePageMeta({ layout: 'pro' })
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.pro-home {
  min-height: 100%;
  background-color: $brand-background-alt;
}
</style>
