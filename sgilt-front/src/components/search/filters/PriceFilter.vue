<template>
  <div class="price-filter">
    <div class="price-range">
      <SgiltPriceInput
        v-model="minPrice"
        :placeholder="$t('texts.minimum')"
        @blur="$emit('blur')"
      />
      <SgiltPriceInput
        v-model="maxPrice"
        :placeholder="$t('texts.maximum')"
        @blur="$emit('blur')"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltPriceInput from '@/components/basics/inputs/SgiltPriceInput.vue'

const minPrice = defineModel('minPrice', { type: Number, required: true })
const maxPrice = defineModel('maxPrice', { type: Number, required: true })

defineEmits<{
  blur: void
}>()
</script>

<style scoped lang="scss">
.price-range {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;

  margin: 0;
  padding: 0;
}
</style>
