<template>
  <div class="partner-thumbnail">
    <div class="partner-category">
      <CategoryChip :label="category" :category="icon" class="category-chip" />
    </div>
    <img :src="imageUrl" alt="Partenaire 1" />

    <h3>{{ title }}</h3>

    <p class="partner-enter-price">A partir de {{ entryPrice }} €</p>
  </div>
</template>

<script setup lang="ts">
import CategoryChip from '@/components/basics/CategoryChip.vue'

defineProps<{
  imageUrl: string
  title: string
  category: string
  icon: string
  entryPrice: number
}>()
</script>

<style lang="scss" scoped>
.partner-thumbnail {
  display: flex;
  flex-direction: column;
  position: relative;

  background: $color-secondary;
  color: $color-primary;

  font-size: 0.8rem;

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

  h3 {
    font-size: 1.2rem;
    font-weight: 500;
  }

  .partner-enter-price {
    font-weight: 300;
  }

  .partner-category {
    position: absolute;
    justify-content: end;

    width: 100%;
    display: flex;
    border-radius: 0.5rem 0.5rem 0 0;

    .category-chip {
      color: $color-secondary;
      background-color: rgba(20, 20, 0, 0.2);
      border: 2px solid $color-secondary;
      gap: 0.2rem;
      margin: 0.5rem;
    }
  }
}
</style>
