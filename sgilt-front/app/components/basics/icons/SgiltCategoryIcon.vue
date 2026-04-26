<script setup lang="ts">
import { markRaw } from 'vue'
import { RestaurantIcon, CameraIcon, StarIcon } from '@remixicons/vue/line'
import IconConfetti from '~/components/icons/IconConfetti.vue'
import IconMusic from '~/components/icons/IconMusic.vue'

const props = defineProps<{
  categoryKey?: string
}>()

const ICONS_MAP: Record<string, any> = {
  all:          markRaw(IconConfetti),
  musique:      markRaw(IconMusic),
  restauration: markRaw(RestaurantIcon),
  photo:        markRaw(CameraIcon),
  services:     markRaw(StarIcon),
}

const activeIcon = computed(() => ICONS_MAP[props.categoryKey ?? 'all'])
</script>

<template>
  <component :is="activeIcon" class="icon-svg" />
</template>
