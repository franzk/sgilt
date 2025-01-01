<template>
  <p class="price-input" tabindex="0" @focus="focusDiv">
    <input
      ref="numberInput"
      type="number"
      step="1"
      :value="displayValue"
      :placeholder="placeholder"
      @beforeinput="beforeInput"
      @input="onInput"
      @keydown="handleKeyDown"
      @blur="$emit('blur')"
    />
    <span>€</span>
  </p>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

// Props
const model = defineModel<number>()

defineProps<{
  placeholder?: string
}>()

defineEmits<{
  blur: void
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

// Handle Shift+Tab in the input
let shiftTab = false
const handleKeyDown = (event: KeyboardEvent) => {
  shiftTab = event.shiftKey && event.key === 'Tab'
}

// Focus the input when the parent div is focused
const numberInput = ref<HTMLInputElement | null>(null)
const focusDiv = () => {
  if (!shiftTab) {
    numberInput.value?.focus()
  } else {
    shiftTab = false
    // TODO : give focus to the previous element
  }
}
</script>

<style scoped lang="scss">
$br: $input-border-radius;
$bw: $border-width-s;
$bc: $shadow-m;

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

  //&:focus-within {
  &:has(input:focus-visible) {
    outline: $focus-outline;
    outline-offset: $focus-outline-offset;
    border-radius: $br;
  }

  input {
    font-size: $font-size-base;
    width: 5rem;
    border-radius: $br 0 0 $br;
    border: $bw solid $bc;

    &:focus {
      outline: none;
    }
  }

  span {
    font-size: $font-size-base;
    align-content: center;
    padding: $spacing-s;
    border-radius: 0 $br $br 0;
    border: $bw solid $bc;
    border-left: none;
    background-color: $color-white;
  }
}
</style>
