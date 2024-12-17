<template>
  <div class="partner-category-button">
    <p class="icon">
      <IconFood v-if="icon === 'food'" />
      <IconMusic v-if="icon === 'music'" />
      <IconPlace v-if="icon === 'place'" />
      <IconPhoto v-if="icon === 'photo'" />
    </p>
    <p class="label">{{ category }}</p>
  </div>
</template>

<script setup lang="ts">
import IconFood from '@/assets/icons/IconFood.vue'
import IconMusic from '@/assets/icons/IconMusic.vue'
import IconPlace from '@/assets/icons/IconPlace.vue'
import IconPhoto from '@/assets/icons/IconPhoto.vue'

defineProps<{
  category: string
  icon: string
}>()
</script>

<style lang="scss" scoped>
.partner-category-button {
  display: flex;
  flex-direction: row;
  align-items: center;

  gap: 0.5rem;
  padding: 0.5rem;

  background: $color-secondary;
  color: $color-accent;

  border-radius: 10rem;

  p {
    margin: 0;
    padding: 0;
    font-weight: 600;
  }

  .icon {
    margin: 0;
    padding: 0;
    svg {
      width: 1rem;
      height: 1rem;
    }
  }
}
</style>
