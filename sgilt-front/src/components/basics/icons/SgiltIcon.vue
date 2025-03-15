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
  ? defineAsyncComponent(
      () => import(`@/components/icons/${props.mobile ? 'mobile' : ''}/Icon${props.icon}.vue`),
    )
  : null
</script>
