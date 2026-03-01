<script setup lang="ts">
import { markRaw } from 'vue'
import IconFood from '@/components/icons/IconFood.vue'
import IconMusic from '@/components/icons/IconMusic.vue'
import IconPhoto from '@/components/icons/IconPhoto.vue'
import IconRocket from '@/components/icons/IconRocket.vue'
import IconStar from '~/components/icons/IconStar.vue'

const props = defineProps<{
  categoryId: string
}>()

// On utilise markRaw ici par sécurité, même si l'objet est statique
// car cela garantit que Vue ne cherchera jamais à le transformer.
const ICONS_MAP: Record<string, any> = {
  1: markRaw(IconRocket),
  2: markRaw(IconMusic),
  3: markRaw(IconFood),
  4: markRaw(IconPhoto),
  5: markRaw(IconStar),
}

// Utilisation d'une computed pour réagir au changement de prop
const activeIcon = computed(() => {
  return ICONS_MAP[props.categoryId] || ICONS_MAP[1]
})
</script>

<template>
  <component :is="activeIcon" class="icon-svg" />
</template>
