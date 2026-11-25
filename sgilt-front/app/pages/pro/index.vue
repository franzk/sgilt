<!--
  Accueil de l'espace prestataire. Vide pour l'instant : les blocs (À contacter, À conclure,
  Vos prochaines prestations, Activité récente) arrivent dans les briques suivantes.
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
