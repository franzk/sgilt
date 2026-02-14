<template>
  <component :is="iconComponent" />
</template>

<script setup lang="ts">
import { defineAsyncComponent } from 'vue'

const props = defineProps<{
  icon: string
  mobile?: boolean
}>()

const iconComponent = props.icon
  ? props.mobile
    ? defineAsyncComponent(() => import(`@/components/icons/mobile/Icon${props.icon}.vue`))
    : defineAsyncComponent(() => import(`@/components/icons/Icon${props.icon}.vue`))
  : null
</script>
