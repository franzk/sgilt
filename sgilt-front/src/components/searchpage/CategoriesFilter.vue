<template>
  <div class="categories-filter">
    <div class="category" v-for="category in categories" :key="category.name">
      <!-- category & tag selection -->
      <div class="category-filter">
        <button
          class="category-entry-button"
          @click="category.tagListExpanded = !category.tagListExpanded"
        >
          <span v-if="!category.tagListExpanded">&#x25B9;</span>
          <span v-if="category.tagListExpanded">&#x25BF;</span>
          <span
            class="category-name"
            :class="{ 'category-selected': category.selection.length > 0 }"
          >
            {{ $t(`categories.${category.name}`).toUpperCase() }}
          </span>
        </button>
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
        class="tags-list"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
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

.tags-list {
  margin-top: $spacing-s;
}

.category-filter {
  display: flex;
  flex-direction: row;
  align-items: center;

  gap: $spacing-m;

  .category-entry-button {
    cursor: pointer;
    background: none;
    border: none;
    border-radius: $border-radius-m;
    .category-name {
      margin-left: $spacing-s;
      font-size: $font-size-m;
      font-weight: 500;
      &:hover {
        color: $color-accent;
        font-weight: 700;
      }
    }
    .category-selected {
      font-weight: 700;
    }
  }

  .selection-tags-list {
    margin: 0;
    padding: 0;
    font-size: $font-size-s;
    font-style: italic;
    text-decoration: underline;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>
