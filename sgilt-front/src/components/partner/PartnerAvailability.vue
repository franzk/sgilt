<template>
  <!-- Disponibilité -->
  <p v-if="availability" class="availability" :class="availabilityClass">
    {{ availabilityText }}
  </p>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  availability?: 'available' | 'option' | 'booked'
}>()

const availabilityText = computed(() => {
  switch (props.availability) {
    case 'available':
      return '🟢 Disponible'
    case 'option':
      return '🟠 Sous option jusqu’à 24h'
    case 'booked':
      return '🔴 Réservé'
    default:
      return ''
  }
})

const availabilityClass = computed(() => {
  switch (props.availability) {
    case 'available':
      return 'available'
    case 'option':
      return 'under-option'
    case 'booked':
      return 'booked'
    default:
      return ''
  }
})
</script>

<style lang="scss" scoped>
.availability {
  font-size: 0.9rem;
  margin-top: $spacing-xs;

  &.available {
    color: $color-available;
  }
  &.under-option {
    color: $color-option;
  }
  &.booked {
    color: $color-booked;
  }
}
</style>
