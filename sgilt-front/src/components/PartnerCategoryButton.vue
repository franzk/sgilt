<template>
  <SgiltChip :label="category" class="partner-category-chip">
    <CategoryIcon :category="icon" />
  </SgiltChip>
</template>

<script setup lang="ts">
import SgiltChip from '@/components/basics/SgiltChip.vue'
import CategoryIcon from '@/components/basics/CategoryIcon.vue'

defineProps<{
  category: string
  icon: string
}>()
</script>

<style lang="scss" scoped>
.partner-category-chip {
  background: $color-secondary;
  color: $color-accent;
}
</style>
