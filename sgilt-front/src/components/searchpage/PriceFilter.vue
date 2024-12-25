<template>
  <div class="price-filter">
    <div class="price-range">
      <SgiltCurrencyInput v-model="minPrice" placeholder="Minimum" />
      <SgiltCurrencyInput v-model="maxPrice" placeholder="Maximum" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SgiltCurrencyInput from '@/components/basics/inputs/SgiltCurrencyInput.vue'

const minPrice = ref(0)
const maxPrice = ref(0)
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
