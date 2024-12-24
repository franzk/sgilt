<template>
  <p class="currency-input">
    <input ref="inputRef" type="number" :value="displayValue" @input="onInput" @blur="onBlur" />
    <span>€</span>
  </p>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { truncateDecimals, formatCurrency } from '@/utils/CurrencyUtils'

const model = defineModel<number>()
const inputRef = ref<HTMLInputElement | null>(null)

// Calcul de la valeur affichée dans le champ
const displayValue = computed(() => (model.value === 0 ? '' : model.value?.toString()))

// Gérer la saisie dans l'input
const onInput = (event: Event) => {
  let value = (event.target as HTMLInputElement).value
  value = truncateDecimals(value, 2)
  model.value = parseFloat(value) || 0
}

// Formater la saisie au blur
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
