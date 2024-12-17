<template>
  <SgiltChip :label="label">
    <CategoryIcon :category="category" class="icon" />
  </SgiltChip>
</template>

<script setup lang="ts">
import SgiltChip from '@/components/basics/SgiltChip.vue'
import CategoryIcon from '@/components/basics/CategoryIcon.vue'

defineProps<{
  label: string
  category: string
}>()
</script>

<style lang="scss" scoped></style>
