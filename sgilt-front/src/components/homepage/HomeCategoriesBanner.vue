<template>
  <div class="categories-banner">
    <h2 class="title">{{ $t('home.categories-banner.title') }}</h2>
    <div class="categories">
      <CategoryChip
        v-for="(category, index) of categories"
        :key="index"
        :category="category"
        class="category-chip"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import CategoryChip from '@/components/basics/CategoryChip.vue'
import type { Category } from '@/domain/Partner'

const categories: Category[] = ['music', 'food', 'place', 'photo']
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

.title {
  margin: 0 0 $spacing-m 0;
  line-height: $line-height-l;
}

.categories {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: $spacing-m;
}

.category-chip {
  background: $color-secondary;
  color: $color-accent;
  box-shadow: 0 1px 3px $ghost;
  border: 2px solid $color-secondary;
}
</style>
