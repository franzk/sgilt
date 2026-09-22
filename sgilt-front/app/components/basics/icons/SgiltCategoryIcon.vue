<script setup lang="ts">
import { markRaw } from 'vue'
import { RestaurantIcon, Building2Icon, StarIcon } from '@remixicons/vue/line'
import IconConfetti from '~/components/icons/IconConfetti.vue'
import IconMusic from '~/components/icons/IconMusic.vue'
import { ALL_CATEGORY_KEY } from '~/utils/constants'

const props = defineProps<{
  categoryKey?: string
}>()

const ICONS_MAP: Record<string, any> = {
  [ALL_CATEGORY_KEY]: markRaw(IconConfetti),
  musique: markRaw(IconMusic),
  restauration: markRaw(RestaurantIcon),
  lieu: markRaw(Building2Icon),
  services: markRaw(StarIcon),
}

const activeIcon = computed(() => ICONS_MAP[props.categoryKey ?? ALL_CATEGORY_KEY])
</script>

<template>
  <component :is="activeIcon" class="icon-svg" />
</template>
