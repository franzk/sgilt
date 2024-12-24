import { computed } from "vue"
<!-- TODO limiter à 2 chiffres après la virgule -->
<template>
  <p class="currency-input">
    <input
      type="number"
      v-model="formattedValue"
      :placeholder="placeholder"
      @beforeinput="beforeInput"
      @input="modelChange"
    />
    <span>€</span>
  </p>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const model = defineModel<number>()

defineProps({
  placeholder: String,
})

const formattedValue = computed(() => model.value || '')

let valueBeforeChange = model.value

const beforeInput = (event: Event) => {
  console.log('beforeInput', (event.target as HTMLInputElement).validity.valid)
  valueBeforeChange = model.value
}

const modelChange = (event: Event) => {
  if (!(event.target as HTMLInputElement).validity.valid) {
    model.value = valueBeforeChange
  } else {
    const value = (event.target as HTMLInputElement).value
    model.value = value ? parseFloat(value.replace(',', '.')) : 0
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
