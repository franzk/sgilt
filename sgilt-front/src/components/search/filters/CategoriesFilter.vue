<template>
  <div class="categories-filter">
    <div class="category" v-for="category in categories" :key="category.name">
      <!-- category & tag selection -->
      <div class="category-filter">
        <button class="category-entry-button" @click="toggleCategory(category.name)">
          <span v-if="isCategoryExpanded(category.name)">&#x25B9;</span>
          <span v-if="!isCategoryExpanded(category.name)">&#x25BF;</span>
          <span
            class="category-name"
            :class="{ 'category-selected': categoryHasSelection(category) }"
          >
            {{ $t(`categories.${category.name}.title`).toUpperCase() }}
          </span>
        </button>
        <!-- selection tags ordered like they are in the category object -->
        <span class="selection-tags-list">{{
          category.selection
            .sort((a, b) => a.id.localeCompare(b.id))
            .map((tag) => tag.name)
            .join(', ')
        }}</span>
      </div>
      <!-- tags checkboxes list -->
      <TagsListFilter
        :tags="category.tags"
        :visible="isCategoryExpanded(category.name)"
        v-model="category.selection"
        class="tags-list"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import TagsListFilter from '@/components/search/filters/TagsListFilter.vue'
import { useCategorysStore } from '@/stores/category.store'
import type { CategoryFilter, CategoryTagFilter } from '@/types/CategoryFilter'

const categories = defineModel<CategoryFilter[]>()

const expandedCategories = ref<string[]>([])

onMounted(() => {
  // map data
  categories.value = useCategorysStore().categories.map((category) => ({
    name: category.name,
    tags: category.tags,
    selection: [] as CategoryTagFilter[],
  }))
})

const toggleCategory = (categoryName: string) => {
  if (expandedCategories.value.includes(categoryName)) {
    expandedCategories.value.splice(expandedCategories.value.indexOf(categoryName), 1)
  } else {
    expandedCategories.value.push(categoryName)
  }
}

const isCategoryExpanded = (categoryName: string) => expandedCategories.value.includes(categoryName)
const categoryHasSelection = (category: CategoryFilter) => category.selection.length > 0
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
