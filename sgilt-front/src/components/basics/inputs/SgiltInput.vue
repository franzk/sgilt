<template>
  <p class="sgilt-input" tabindex="0" @focus="focusDiv">
    <div class="icon" v-if="$slots.icon">
      <slot name="icon" />
    </div>
    <input
      ref="numberInput"
      :type="type"
      step="1"
      :value="displayValue"
      :placeholder="placeholder"
      @beforeinput="beforeInput"
      @input="onInput"
      @keydown="handleKeyDown"
      @blur="$emit('blur')"
    />
    <span v-if="symbol">{{ symbol }}</span>
  </p>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

// Props
const numberModel = defineModel('number-model', { type: Number })

const textModel = defineModel('text-model', { type: String })

const props = defineProps<{
  placeholder?: string
  symbol?: string
  type: 'number' | 'text'
}>()

defineEmits<{
  blur: void
}>()

// show empty string if value is 0
const displayValue = computed(() =>
  props.type === 'text'
    ? textModel.value
    : numberModel.value === 0
      ? ''
      : numberModel.value?.toString(),
)

// onInput handler : update model.value
const onInput = (event: Event) => {
  const value = (event.target as HTMLInputElement).value
  if (props.type === 'text') {
    textModel.value = value
  } else {
    numberModel.value = value ? parseFloat(value) : 0
  }
}

// beforeInput handler : prevent input of decimal separator
const beforeInput = (event: Event) => {
  if (props.type === 'number' && ['.', ','].includes((event as InputEvent).data || '')) {
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

.sgilt-input {
  display: flex;
  flex-direction: row;
  align-content: center;
  height: 3rem;
  position: relative;

  .icon {
    position: absolute;
    left: 0.4rem;
    top: 0.8rem;
    bottom: 0;
    height: 1.3rem;
    width: 1.3rem;
  }

  &:has(input:focus-visible) {
    outline: $focus-outline;
    outline-offset: $focus-outline-offset;
    border-radius: $br;
  }

  &:not(:has(span)) {
    input {
      border-radius: $br;
    }
  }

  &:has(.icon) {
    input {
      padding-left: 2rem;
    }
  }

  input {
    font-size: $font-size-base;
    width: 100%;

    border-radius: $br 0 0 $br;
    border: $bw solid $bc;
    // text-align: right;
    flex: 1;

    &:focus {
      outline: none;
    }
  }

  input[type='number'] {
    text-align: right;
    appearance: textfield;
    -moz-appearance: textfield;

    padding: $spacing-m;
  }

  input[type='text'] {
    text-align: left;
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
