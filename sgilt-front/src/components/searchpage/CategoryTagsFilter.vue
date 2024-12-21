<template>
  <div class="category-tags-filter">
    <div class="category" v-for="category in categories" :key="category.name">
      <div class="category-title">
        <CategoryChip :category="category" />
      </div>
      <div class="tag" v-for="tag in category.tags" :key="tag.name">
        <input type="checkbox" :id="tag.name" :value="tag.checked" />
        <label :for="tag.name">{{ tag.name }}</label>
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
    tags: category.tags.map((tag) => ({
      id: tag.id,
      name: tag.name,
      checked: false,
    })),
  }))
})
</script>

<style scoped lang="scss">
.category-tags-filter {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  .tag {
    margin-left: $spacing-m;
  }
}
</style>
