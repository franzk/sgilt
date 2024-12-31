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
  text-align: center;
}

.categories {
  display: grid;

  grid-template-columns: repeat(auto-fit, minmax(14rem, 1fr));
  gap: $spacing-xl;
  padding: 0 $spacing-xl;

  // 4 columns
  grid-template-columns: repeat(4, 1fr);

  @include respond-to(tablet) {
    gap: $spacing-m;
    padding: 0 $spacing-m;
  }

  @include respond-to(mobile) {
    // 2 X 2
    grid-template-columns: repeat(2, 1fr);
    grid-template-rows: repeat(2, auto);
  }
}

.category-card {
  text-decoration: none;
}
</style>
