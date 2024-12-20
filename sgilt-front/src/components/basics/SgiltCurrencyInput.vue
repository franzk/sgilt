<template>
  <p class="currency-input">
    <input
      type="number"
      :value="modelValue"
      :placeholder="placeholder"
      size="2"
      @input="$emit('update:modelValue', $event.target.value)"
    />
    <span>€</span>
  </p>
</template>

<script setup lang="ts">
defineProps({
  modelValue: String,
  placeholder: String,
})

defineEmits(['update:modelValue'])
</script>

<style scoped lang="scss">
$br: $border-radius-m;
$bw: $border-width-s;

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

  input {
    font-size: $font-size-m;
    width: 5rem;
    border-radius: $br 0 0 $br;
    border: $bw solid $color-primary;
    outline-color: $color-primary;
  }

  span {
    border: $bw solid #ccc;
    font-size: $font-size-m;
    align-content: center;
    padding: $spacing-s;
    border-radius: 0 $br $br 0;
    border: 1px solid $color-primary;
    border-left: none;
    background-color: white;
  }
}
</style>
