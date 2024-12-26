<template>
  <p class="price-input">
    <input
      type="number"
      step="1"
      :value="displayValue"
      :placeholder="placeholder"
      @beforeinput="beforeInput"
      @input="onInput"
    />
    <span>€</span>
  </p>
</template>

<script setup lang="ts">
import { computed } from 'vue'

// Props
const model = defineModel<number>()

defineProps<{
  placeholder?: string
}>()

// show empty string if value is 0
const displayValue = computed(() => (model.value === 0 ? '' : model.value?.toString()))

// onInput handler : update model.value
const onInput = (event: Event) => {
  const value = (event.target as HTMLInputElement).value
  model.value = value ? parseFloat(value) : 0
}

// beforeInput handler : prevent input of decimal separator
const beforeInput = (event: Event) => {
  if (['.', ','].includes((event as InputEvent).data || '')) {
    event.preventDefault()
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

.price-input {
  display: flex;
  flex-direction: row;
  align-content: center;

  height: 2.5rem;

  input {
    font-size: $font-size-m;
    width: 5rem;
    border-radius: $br 0 0 $br;
    border: $bw solid $bc;
    // outline-color: $color-primary;
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
