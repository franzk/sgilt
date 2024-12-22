<template>
  <template v-if="visible">
    <div class="tag" v-for="tag in tags" :key="tag.id">
      <input type="checkbox" :id="tag.id" :value="tag" v-model="selection" />
      <label :for="tag.id">{{ tag.name }}</label>
    </div>
  </template>
</template>

<script setup lang="ts">
import type { SearchCategoryTag } from '@/types/SearchCategory'

const selection = defineModel<SearchCategoryTag[]>()

defineProps<{
  tags: SearchCategoryTag[]
  visible: boolean
}>()
</script>

<style scoped lang="scss">
.filter-tags-list {
  margin: 0;
  padding: 0;
  font-size: $font-size-s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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
