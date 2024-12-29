<template>
  <div class="categories-filter">
      <TagsListFilter :tags="tags" v-model="selection" class="tags-list" />
    </!--div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import TagsListFilter from '@/components/search/filters/TagsListFilter.vue'
import type { TagFilter } from '@/types/TagFilter'
import { useTagsStore } from '@/stores/tag.store'

// Selected tags
const selection = defineModel<TagFilter[]>()

// List of all tags
const tags = ref<TagFilter[]>(useTagsStore().tags.map((tag) => ({ id: tag.id, name: tag.name, category: tag.category })))

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
