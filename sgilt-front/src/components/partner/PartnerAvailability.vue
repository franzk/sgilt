<template>
  <!-- Disponibilité -->
  <p v-if="availability" class="availability" :class="availabilityClass">
    {{ availabilityText }}
  </p>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps<{
  availability?: string
}>()

const availabilityText = computed(() => {
  if (['available', 'option', 'booked'].includes(props.availability ?? '')) {
    return t(`partner.availability.${props.availability}`)
  } else {
    return ''
  }
})

const availabilityClass = computed(() => {
  if (['available', 'option', 'booked'].includes(props.availability ?? '')) {
    return props.availability
  } else {
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
  &.option {
    color: $color-option;
  }
  &.booked {
    color: $color-booked;
  }
}
</style>
