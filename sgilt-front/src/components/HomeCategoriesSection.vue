<template>
  <div class="categories-banner">
    <h2 class="categories-title">{{ $t('home.categories-banner.title') }}</h2>
    <div class="categories">
      <CategoryCard
        v-for="(category, index) of categories"
        :key="index"
        :categoryName="category.name"
        :imageUrl="category.imageUrl"
        class="category-card"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import CategoryCard from '@/components/basics/cards/CategoryCard.vue'
import { useCategorysStore } from '@/stores/category.store'

const categories = useCategorysStore().categories
</script>

<style lang="scss" scoped>
.categories-banner {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $spacing-l 0 $spacing-xl 0;
  background-color: $color-accent;
  color: $color-secondary;
  box-shadow: 0 -4px 6px $ghost;
}

.categories-title {
  margin: 0 0 $spacing-m 0;
}

.categories {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  margin: 0 $spacing-m;
}

.category-card {
  background: $color-secondary;
  color: $color-accent;
  box-shadow: 0 1px 3px $ghost;
  margin: $spacing-m;
  width: 12rem;
  border: 2px solid $color-secondary;
}
</style>
