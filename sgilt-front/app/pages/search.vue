<template>
  <PrestataireSearch :showDateFilter="showDateFilter" @select="navigateTo(`/${$event.slug}`)" />
</template>

<script setup lang="ts">
import PrestataireSearch from '~/components/prestataire/PrestataireSearch.vue'

const { currentFlow } = useFlow()

const showDateFilter = computed(() => currentFlow.value !== 'add-prestataire')
</script>
