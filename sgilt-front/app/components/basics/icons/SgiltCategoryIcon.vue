<script setup lang="ts">
import { markRaw } from 'vue'
import { RestaurantIcon, CameraIcon, StarIcon } from '@remixicons/vue/line'
import IconConfetti from '~/components/icons/IconConfetti.vue'
import IconMusic from '~/components/icons/IconMusic.vue'

const props = defineProps<{
  categoryId?: string
}>()

// On utilise markRaw ici par sécurité, même si l'objet est statique
// car cela garantit que Vue ne cherchera jamais à le transformer.
const ICONS_MAP: Record<string, any> = {
  '1': markRaw(IconConfetti),
  '2': markRaw(IconMusic),
  '3': markRaw(RestaurantIcon),
  '4': markRaw(CameraIcon),
  '5': markRaw(StarIcon),
}

// Utilisation d'une computed pour réagir au changement de prop
const activeIcon = computed(() => {
  return ICONS_MAP[props.categoryId ?? '1']
})
</script>

<template>
  <component :is="activeIcon" class="icon-svg" />
</template>
