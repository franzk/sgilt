<template>
  <div v-for="(category, index) in categories" :key="index" class="category">
    <h3
      class="category-title"
      tabindex="0"
      @click="toogleCategory(category)"
      @keydown.space="toogleCategory(category)"
      @keydown.enter="toogleCategory(category)"
    >
      <span :class="{ 'category-selected': tagsSelectedInCategoryCount(category) > 0 }">
        {{ $t(`categories.${category}.title`).toUpperCase() }}
        {{
          tagsSelectedInCategoryCount(category) ? `(${tagsSelectedInCategoryCount(category)})` : ''
        }}
      </span>
      <span v-if="!isCategoryExpanded(category)" class="expand-category">&#8897;</span>
      <span v-if="isCategoryExpanded(category)" class="collapse-category">&#8896;</span>
    </h3>

    <div v-if="isCategoryExpanded(category)" class="tags-list">
      <div class="tag" v-for="tag in tags.filter((tag) => tag.category === category)" :key="tag.id">
        <SgiltCheckbox v-model="selection" :id="tag.id" :value="tag" :label="tag.name" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltCheckbox from '@/components/basics/inputs/SgiltCheckbox.vue'
import type { TagFilter } from '@/types/TagFilter'
import { computed, onMounted, ref } from 'vue'
import { distinctByKey } from '@/utils/ArrayUtils'
import { useTagsStore } from '@/stores/tag.store'

// Selected tags
const selection = defineModel<TagFilter[]>()

// Tags from store
const tags = ref<TagFilter[]>(
  useTagsStore().tags.map((tag) => ({ id: tag.id, name: tag.name, category: tag.category })),
)

// categories computed from tags
const categories = computed(() => {
  return distinctByKey(tags.value, 'category')
})

// Count of selected tags in a category
const tagsSelectedInCategoryCount = (category: string): number => {
  return selection.value?.filter((tag) => tag.category === category).length || 0
}

// Expand or collapse a category
const expandedCategories = ref<string[]>([])
const toogleCategory = (category: string) => {
  if (expandedCategories.value.includes(category)) {
    expandedCategories.value = expandedCategories.value.filter((c) => c !== category)
  } else {
    expandedCategories.value.push(category)
  }
}
const isCategoryExpanded = (category: string) => expandedCategories.value.includes(category)
// Expand categories with selected tags (herited from route) on mount
onMounted(() => {
  expandedCategories.value = categories.value.filter(
    (category) => tagsSelectedInCategoryCount(category) > 0,
  )
})
</script>

<style scoped lang="scss">
.category {
  margin: 0 $spacing-l 0 0;
  padding: 0;
}

.category-selected {
  font-weight: 600;
}

.category-title {
  cursor: pointer;
  margin: 0;
  padding: $spacing-s 0;
  border-bottom: $border-width-s solid $color-accent;
  display: flex;
  justify-content: space-between;
  .expand-category,
  .collapse-category {
    color: $color-accent;
  }

  &:focus-visible {
    outline: none;
    border-width: $border-width-m;
  }
}

.tags-list {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-top: $spacing-s;
}
.tag {
  margin: 0 $spacing-m 0 $spacing-m;
}

input[type='checkbox'] {
  background-color: $color-accent;
}
</style>
