<template>
  <IconFood v-if="category === 'food'" />
  <IconMusic v-if="category === 'music'" />
  <IconPlace v-if="category === 'place'" />
  <IconPhoto v-if="category === 'photo'" />
</template>

<script setup lang="ts">
import IconFood from '@/assets/icons/IconFood.vue'
import IconMusic from '@/assets/icons/IconMusic.vue'
import IconPlace from '@/assets/icons/IconPlace.vue'
import IconPhoto from '@/assets/icons/IconPhoto.vue'

defineProps<{
  category: string
}>()
</script>
