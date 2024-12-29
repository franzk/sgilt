<template>
  <div v-for="(category, index) in categories" :key="index" class="category">
    <p>
      {{ category }}
      {{
        tagsSelectedInCategoryCount(category) ? `(${tagsSelectedInCategoryCount(category)})` : ''
      }}
    </p>
    <div class="tags-list">
      <div class="tag" v-for="tag in tags.filter((tag) => tag.category === category)" :key="tag.id">
        <SgiltCheckbox
          v-model="selection"
          :id="tag.id"
          :value="tag"
          :label="`${tag.name} - ${tag.category}`"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltCheckbox from '@/components/basics/inputs/SgiltCheckbox.vue'
import type { TagFilter } from '@/types/TagFilter'
import { computed } from 'vue'
import { distinctByKey } from '@/utils/ArrayUtils'

// Selected tags
const selection = defineModel<TagFilter[]>()

// Props : list of all tags
const props = defineProps<{
  tags: TagFilter[]
}>()

// categories computed from tags
const categories = computed(() => {
  return distinctByKey(props.tags, 'category')
})

// Count of selected tags in a category
const tagsSelectedInCategoryCount = (category: string): number => {
  return selection.value?.filter((tag) => tag.category === category).length || 0
}
</script>

<style scoped lang="scss">
.tags-list {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.tag {
  margin: 0 $spacing-m 0 $spacing-m;
}

input[type='checkbox'] {
  background-color: $color-accent;
}
</style>
