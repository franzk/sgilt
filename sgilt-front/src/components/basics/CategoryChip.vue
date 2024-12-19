<template>
  <SgiltChip :label="$t(`categories.${category}`)">
    <component :is="icon" class="icon" />
  </SgiltChip>
</template>

<script setup lang="ts">
import SgiltChip from '@/components/basics/SgiltChip.vue'
import IconFood from '@/components/icons/IconFood.vue'
import IconMusic from '@/components/icons/IconMusic.vue'
import IconPlace from '@/components/icons/IconPlace.vue'
import IconPhoto from '@/components/icons/IconPhoto.vue'

const props = defineProps<{
  category: 'food' | 'music' | 'place' | 'photography'
}>()

const icon = {
  food: IconFood,
  music: IconMusic,
  place: IconPlace,
  photography: IconPhoto,
}[props.category]
</script>
