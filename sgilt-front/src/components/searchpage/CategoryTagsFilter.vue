<template>
  <div class="category-tags-filter">
    <div class="category" v-for="category in categories" :key="category.name">
      <div class="category-filter">
        <CategoryChip :categoryName="category.name" class="category-chip" />
        <span class="filter-tags-list">{{ category.selection.join(', ') }}</span>
      </div>
      <div class="tag" v-for="tag in category.tags" :key="tag">
        <input type="checkbox" :id="tag" :value="tag" v-model="category.selection" />
        <label :for="tag">{{ tag }}</label>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import CategoryChip from '@/components/basics/CategoryChip.vue'
import { useCategorysStore } from '@/stores/category.store'
import type { SearchCategory } from '@/types/SearchCategory'

const categories = ref<SearchCategory[]>([])

onMounted(() => {
  // fetch data
  useCategorysStore().fetchTags()
  // map data
  categories.value = useCategorysStore().categories.map((category) => ({
    name: category.name,
    tags: category.tags.map((tag) => tag.name),
    selection: [] as string[],
  }))
})
</script>

<style scoped lang="scss">
.category-filter {
  display: flex;
  flex-direction: row;
  align-items: center;

  gap: 1rem;

  .category-chip {
    color: $color-primary;
    border: $border-width-s solid $color-primary;
    gap: $spacing-xs;
  }

  .filter-tags-list {
    margin: 0;
    padding: 0;
    font-size: $font-size-s;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.category-tags-filter {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  .tag {
    margin-left: $spacing-m;
  }
}
</style>
