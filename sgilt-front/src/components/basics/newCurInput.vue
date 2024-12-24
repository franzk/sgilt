<template>
  <div>
    <input
      type="number"
      :value="displayValue"
      @input="handleInput"
      @blur="formatInput"
      ref="inputRef"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const model = defineModel<number>()
const inputValue = ref('')
const inputRef = ref<HTMLInputElement | null>(null)

// Calcul de la valeur affichée dans le champ
const displayValue = computed(() => (model.value === 0 ? '' : inputValue.value))

// Gérer la saisie dans l'input
const handleInput = (event: Event) => {
  let value = (event.target as HTMLInputElement).value
  // Limiter à deux chiffres après la virgule
  if (value.includes('.')) {
    const [integer, decimals] = value.split('.')
    value = `${integer}.${decimals.slice(0, 2)}`
  }
  // Mettre à jour l'état local et émettre la nouvelle valeur
  inputValue.value = value
  model.value = parseFloat(value) || 0
}

// Formater la saisie au blur
const formatInput = () => {
  // const value = parseFloat(inputValue.value)
  console.log('blur', inputValue.value, model.value)
  //inputValue.value = '2222'
  // inputValue.value = model.value?.toString() || ''
  // refresh the display value
  // model.value = 12
  if (inputRef.value) {
    inputRef.value.value = model.value ? model.value.toString() : ''
  }
}
</script>

<style scoped>
input {
  width: 100px;
}
</style>
