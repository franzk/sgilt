<template>
  <div class="p-10 bg-white" style="position: relative; z-index: 9999">
    <USelectMenu v-model="modelValue" :options="['Test 1', 'Test 2']" class="w-full" />
    <p>Valeur actuelle : {{ modelValue }}</p>
  </div>
</template>

<script setup lang="ts">
const modelValue = ref<string>('Test 1')
</script>
