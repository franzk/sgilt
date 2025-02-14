<template>
  <div class="price-filter">
    <SgiltInput
      symbol="€"
      type="number"
      v-model:number-model="minPrice"
      :placeholder="$t('texts.minimum')"
      @blur="$emit('blur')"
    />
    <SgiltInput
      symbol="€"
      type="number"
      v-model:number-model="maxPrice"
      :placeholder="$t('texts.maximum')"
      @blur="$emit('blur')"
    />
  </div>
</template>

<script setup lang="ts">
import SgiltInput from '@/components/basics/inputs/SgiltInput.vue'

const minPrice = defineModel('minPrice', { type: Number, required: true })
const maxPrice = defineModel('maxPrice', { type: Number, required: true })

defineEmits<{
  blur: void
}>()
</script>

<style scoped lang="scss">
.price-filter {
  display: flex;
  flex-direction: row;
  gap: $spacing-m;

  margin: 0;
  padding: 0;
}
</style>
