<template>
  <div class="categories-banner">
    <h2 class="categories-title">{{ $t('home.categories-banner.title') }}</h2>
    <div class="categories">
      <div v-for="(category, index) of categories" :key="index">
        <router-link
          :to="{ name: 'search', query: { category: category.name } }"
          class="category-card"
        >
          <CategoryCard :categoryName="category.name" :imageUrl="category.imageUrl" />
        </router-link>
      </div>
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
  color: $color-primary;
  box-shadow: 0 -4px 6px $shadow-s;
}

.categories-title {
  margin: 0 0 $spacing-m 0;
}

.categories {
  display: grid;
  gap: $spacing-l;

  grid-template-columns: repeat(auto-fit, minmax(14rem, 1fr));

  @media (min-width: 63rem) {
    // 4 columns
    grid-template-columns: repeat(4, 1fr);
  }

  @media (max-width: 70rem) and (min-width: 40rem) {
    // 2 columns
    grid-template-columns: repeat(2, 1fr);
    grid-template-rows: repeat(2, auto);
  }

  @media (max-width: 39.9rem) {
    // 1 column
    grid-template-columns: 1fr;
    grid-template-rows: repeat(4, auto);
  }
}

.category-card {
  width: 16rem;
  text-decoration: none;
}
</style>
