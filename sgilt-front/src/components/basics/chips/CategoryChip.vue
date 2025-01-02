<template>
  <SgiltChip
    :label="$t(`categories.${categoryName}.title`)"
    @click="$emit('click')"
    class="category-chip small-font"
  >
    <component :is="icon" class="icon" />
  </SgiltChip>
</template>

<script setup lang="ts">
import SgiltChip from '@/components/basics/chips/SgiltChip.vue'
import IconFood from '@/components/icons/IconFood.vue'
import IconMusic from '@/components/icons/IconMusic.vue'
import IconPlace from '@/components/icons/IconPlace.vue'
import IconPhoto from '@/components/icons/IconPhoto.vue'
import type { Component } from 'vue'

const props = defineProps<{
  categoryName: string
}>()

defineEmits(['click'])

const icons: Record<string, Component> = {
  food: IconFood,
  music: IconMusic,
  place: IconPlace,
  photo: IconPhoto,
}

const icon = icons[props.categoryName]
</script>

<style scoped lang="scss">
.category-chip {
  cursor: pointer;
  color: $color-secondary;
  background-color: $shadow-l;
  border: $border-width-s solid $color-secondary;
  gap: $spacing-xs;
  margin: $spacing-s;
}
</style>
