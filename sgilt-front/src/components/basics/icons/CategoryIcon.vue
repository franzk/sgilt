<template><component :is="icon" class="icon" /></template>

<script setup lang="ts">
import IconFood from '@/components/icons/IconFood.vue'
import IconMusic from '@/components/icons/IconMusic.vue'
import IconPlace from '@/components/icons/IconPlace.vue'
import IconPhoto from '@/components/icons/IconPhoto.vue'
import type { Component } from 'vue'

const props = defineProps<{
  categoryName: string
}>()

const icons: Record<string, Component> = {
  food: IconFood,
  music: IconMusic,
  place: IconPlace,
  photo: IconPhoto,
}

const icon = icons[props.categoryName]
</script>
