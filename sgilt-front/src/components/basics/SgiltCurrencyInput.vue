<template>
  <p class="currency-input">
    <input type="number" v-model="model" :placeholder="placeholder" />
    <span>€</span>
  </p>
</template>

<script setup lang="ts">
const model = defineModel()

defineProps({
  placeholder: String,
})
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
