<template>
  <div class="partner-thumbnail">
    <img :src="imageUrl" alt="Partenaire 1" />
    <h3>{{ title }}</h3>
    <p class="partner-category">Catégorie : {{ category }}</p>
    <p class="partner-enter-price">A partir de {{ entryPrice }} €</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  imageUrl: string
  title: string
  category: string
  entryPrice: number
}>()
</script>

<style lang="scss" scoped>
.partner-thumbnail {
  display: flex;
  flex-direction: column;

  padding: 0.5rem;

  background: $color-secondary;
  color: $color-primary;

  img {
    width: 100%;
    height: auto;
    border-radius: 0.5rem;
    cursor: pointer;
  }

  h3,
  p {
    margin: 0;
    padding: 0.1rem;
  }

  .partner-category {
    font-weight: 300;
    font-style: italic;
  }

  .partner-enter-price {
    font-weight: 300;
  }
}
</style>
