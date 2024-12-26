<template>
  <div v-if="visible" class="tags-list">
    <div class="tag" v-for="tag in tags" :key="tag.id">
      <SgiltCheckbox v-model="selection" :id="tag.id" :value="tag" :label="tag.name" />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { SearchCategoryTag } from '@/types/SearchCategory'
import SgiltCheckbox from '@/components/basics/inputs/SgiltCheckbox.vue'

const selection = defineModel<SearchCategoryTag[]>()

defineProps<{
  tags: SearchCategoryTag[]
  visible: boolean
}>()
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
