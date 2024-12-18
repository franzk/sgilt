<template>
  <IconFood v-if="category === 'food'" />
  <IconMusic v-if="category === 'music'" />
  <IconPlace v-if="category === 'place'" />
  <IconPhoto v-if="category === 'photography'" />
</template>

<script setup lang="ts">
import IconFood from '@/components/icons/IconFood.vue'
import IconMusic from '@/components/icons/IconMusic.vue'
import IconPlace from '@/components/icons/IconPlace.vue'
import IconPhoto from '@/components/icons/IconPhoto.vue'

defineProps<{
  category: string
}>()
</script>
