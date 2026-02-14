<template>
  <textarea :placeholder="placeholder" v-model="textValue" @blur="onBlur" />
</template>

<script setup lang="ts">
import { ref } from 'vue'

const text = defineModel<string>()

defineProps<{ placeholder?: string }>()

const textValue = ref(text.value)

const onBlur = () => {
  text.value = textValue.value
}
</script>
