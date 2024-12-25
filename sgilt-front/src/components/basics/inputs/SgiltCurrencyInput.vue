<template>
  <p class="currency-input">
    <input
      ref="inputRef"
      type="number"
      :value="displayValue"
      @input="onInput"
      @blur="onBlur"
      :placeholder="placeholder"
    />
    <span>€</span>
  </p>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { truncateDecimals, formatCurrency } from '@/utils/CurrencyUtils'

// Props
const model = defineModel<number>()

defineProps<{
  placeholder?: string
}>()

// Refs
const inputRef = ref<HTMLInputElement | null>(null)

// show empty string if value is 0
const displayValue = computed(() => (model.value === 0 ? '' : model.value?.toString()))

// input event
const onInput = (event: Event) => {
  let value = (event.target as HTMLInputElement).value
  value = truncateDecimals(value, 2)
  model.value = parseFloat(value) || 0
}

// format on blur
const onBlur = () => {
  if (inputRef.value) {
    inputRef.value.value = formatCurrency(model.value)
  }
}
</script>

<style scoped lang="scss">
$br: $input-border-radius;
$bw: $border-width-s;
$bc: $shadow;

p {
  margin: 0;
  padding: 0;
}

input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type='number'] {
  appearance: textfield;
  -moz-appearance: textfield;

  padding: $spacing-m;
}

.currency-input {
  display: flex;
  flex-direction: row;
  align-content: center;

  height: 2.5rem;

  input {
    font-size: $font-size-m;
    width: 5rem;
    border-radius: $br 0 0 $br;
    border: $bw solid $bc;
    outline-color: $color-primary;
  }

  span {
    border: $bw solid #ccc;
    font-size: $font-size-m;
    align-content: center;
    padding: $spacing-s;
    border-radius: 0 $br $br 0;
    border: bw solid $bc;
    border-left: none;
    background-color: white;
  }
}
</style>
