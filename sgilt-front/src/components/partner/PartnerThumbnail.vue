<template>
  <div class="partner-thumbnail">
    <div class="partner-category">
      <CategoryChip :category="icon" class="category-chip" />
    </div>
    <img :src="imageUrl" alt="Partenaire 1" />

    <h3>{{ title }}</h3>

    <p class="partner-description">Description</p>
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

  font-size: $font-size-s;

  img {
    width: 100%;
    height: auto;
    border-radius: $border-radius-s;
    cursor: pointer;
  }

  h3,
  p {
    margin: 0;
    padding: $spacing-xxs;
  }

  h3 {
    font-size: $font-size-l;
    font-weight: 500;
    margin-top: $spacing-xs;
  }

  .partner-enter-price {
    font-weight: 300;
  }

  .partner-category {
    position: absolute;
    justify-content: end;

    width: 100%;
    display: flex;
    border-radius: $border-radius-s $border-radius-s 0 0;

    .category-chip {
      color: $color-secondary;
      background-color: $shadow;
      border: $border-width-s solid $color-secondary;
      gap: $spacing-xs;
      margin: $spacing-s;
    }
  }
}
</style>
