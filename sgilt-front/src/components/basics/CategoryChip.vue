<template>
  <SgiltChip :label="$t(`categories.${category.name}`)">
    <component :is="icon" class="icon" />
  </SgiltChip>
</template>

<script setup lang="ts">
import type { Category } from '@/domain/Category'
import SgiltChip from '@/components/basics/SgiltChip.vue'
import IconFood from '@/components/icons/IconFood.vue'
import IconMusic from '@/components/icons/IconMusic.vue'
import IconPlace from '@/components/icons/IconPlace.vue'
import IconPhoto from '@/components/icons/IconPhoto.vue'
import type { Component } from 'vue'

const props = defineProps<{
  category: Category
}>()

const icons: Record<string, Component> = {
  food: IconFood,
  music: IconMusic,
  place: IconPlace,
  photo: IconPhoto,
}

const icon = icons[props.category.name]
</script>
