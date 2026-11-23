<!--
  Titre de section de l'espace client : titre serif, sous-titre optionnel, trait jaune dessous.
  Utilisé pour « Mes événements » (accueil /app) et « À suivre » (ToFollowList).
-->
<template>
  <div class="section-heading">
    <h2 class="title">{{ title }}</h2>
    <p v-if="subtitle" class="subtitle">{{ subtitle }}</p>
    <span class="underline" aria-hidden="true" />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  subtitle?: string
}>()
</script>

<style scoped lang="scss">
.section-heading {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 2rem;
    font-weight: 600;
    color: $brand-primary;
    margin: 0;
    line-height: 1.1;
  }

  .subtitle {
    font-family: 'Inter', sans-serif;
    font-size: 0.95rem;
    color: $text-secondary;
    margin: 0;
  }

  .underline {
    width: 3.5rem;
    height: 0.2rem;
    border-radius: 999px;
    background: $brand-accent;
    margin-top: $spacing-xs;
  }
}
</style>
