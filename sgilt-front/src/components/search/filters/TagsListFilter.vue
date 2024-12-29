<template>
  <div class="tags-list">
    <div class="tag" v-for="tag in tags" :key="tag.id">
      <SgiltCheckbox
        v-model="selection"
        :id="tag.id"
        :value="tag"
        :label="`${tag.name} - ${tag.category}`"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltCheckbox from '@/components/basics/inputs/SgiltCheckbox.vue'
import type { TagFilter } from '@/types/TagFilter'

const selection = defineModel<TagFilter[]>()

console.log('selection', selection.value)

defineProps<{
  tags: TagFilter[]
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
