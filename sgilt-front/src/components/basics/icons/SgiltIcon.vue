<template>
  <component :is="iconComponent" />
</template>

<script setup lang="ts">
import { defineAsyncComponent } from 'vue'

const props = defineProps<{
  icon: string
}>()

const iconComponent = props.icon
  ? defineAsyncComponent(() => import(`@/components/icons/Icon${props.icon}.vue`))
  : null
</script>
