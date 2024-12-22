<template>
  <div class="categories-filter">
    <div class="category" v-for="category in categories" :key="category.name">
      <!-- chip & selection -->
      <div class="category-filter">
        <CategoryChip
          :categoryName="category.name"
          class="category-chip"
          @click="category.tagListExpanded = !category.tagListExpanded"
        />
        <span class="selection-tags-list">{{
          category.selection
            .sort((a, b) => a.id.localeCompare(b.id))
            .map((tag) => tag.name)
            .join(', ')
        }}</span>
      </div>
      <!-- tags list -->
      <TagsListFilter
        :tags="category.tags"
        :visible="category.tagListExpanded"
        v-model="category.selection"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import CategoryChip from '@/components/basics/CategoryChip.vue'
import TagsListFilter from '@/components/searchpage/TagsListFilter.vue'
import { useCategorysStore } from '@/stores/category.store'
import type { SearchCategory, SearchCategoryTag } from '@/types/SearchCategory'

// -- Data --
const categories = ref<SearchCategory[]>([])

onMounted(() => {
  // fetch data
  useCategorysStore().fetchTags()
  // map data
  categories.value = useCategorysStore().categories.map((category) => ({
    name: category.name,
    tags: category.tags,
    selection: [] as SearchCategoryTag[],
    tagListExpanded: false,
  }))
})
</script>

<style scoped lang="scss">
.categories-filter {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

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

  .selection-tags-list {
    margin: 0;
    padding: 0;
    font-size: $font-size-s;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>
