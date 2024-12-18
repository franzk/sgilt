<template>
  <SgiltChip :label="$t(`categories.${category}`)">
    <CategoryIcon :category="category" class="icon" />
  </SgiltChip>
</template>

<script setup lang="ts">
import SgiltChip from '@/components/basics/SgiltChip.vue'
import CategoryIcon from '@/components/basics/CategoryIcon.vue'

defineProps<{
  category: string
}>()
</script>
