<template>
  <div class="partner-prices-form">
    <SgiltSelect :options="pricesOptions" v-model="selectedOption" />
    <div class="price">{{ prices.find((price) => price.id === selectedOption)?.price }} €</div>
  </div>
</template>

<script setup lang="ts">
import SgiltSelect from '@/components/basics/inputs/SgiltSelect.vue'
import { ref } from 'vue'

const selectedOption = ref<string>('1')

const prices = [
  { id: '1', name: 'Concert', description: 'Concert de 1h', price: 100 },
  { id: '', name: 'Concert', description: 'Concert de 2h', price: 200 },
  { id: '3', name: 'Concert', description: 'Concert de 3h', price: 300 },
  { id: '4', name: 'Concert', description: 'Concert de 4h', price: 400 },
  { id: '5', name: 'Concert', description: 'Concert de 5h', price: 500 },
]

const pricesOptions = prices.map((price) => ({
  value: price.id,
  label: price.description,
}))
</script>
