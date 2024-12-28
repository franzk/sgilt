<template>
  <div class="categories-banner">
    <h2 class="categories-title">{{ $t('home.categories-banner.title') }}</h2>
    <ul class="categories">
      <li v-for="(category, index) of categories" :key="index">
        <router-link
          :to="{ name: 'search', query: { category: category.name } }"
          class="category-card"
        >
          <CategoryCard :categoryName="category.name" :imageUrl="category.imageUrl" />
        </router-link>
      </li>
    </ul>
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
  box-shadow: 0 -4px 6px $shadow-s;
}

.categories-title {
  margin: 0 0 $spacing-m 0;
}

.categories {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  margin: 0 $spacing-m;
  list-style-type: none;
}

.category-card {
  margin: $spacing-l;
  width: 14rem;
  text-decoration: none;
}
</style>
