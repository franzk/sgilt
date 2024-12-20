<template>
  <h2>Tarif</h2>
  <div class="price-range">
    <div class="price-range-min">
      <span>Minimum </span><input type="range" min="0" class="slider" id="min" v-model="minPrice" />
    </div>
    <div class="price-range-max">
      <span>Maximum </span
      ><input type="range" min="0" max="10000" class="slider" id="max" v-model="maxPrice" />
    </div>
    <p>De {{ minPrice }} à {{ maxPrice }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const minPrice = ref(0)
const maxPrice = ref(0)
</script>

<style></style>
