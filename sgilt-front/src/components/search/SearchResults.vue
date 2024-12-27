<template>
  <div>{{ searchStore.results.length }} résultat(s)</div>
  <PartnerGrid :partners="searchStore.results" />
</template>

<script setup lang="ts">
import PartnerGrid from '@/components/partner/PartnerGrid.vue'
import { useSearchStore } from '@/stores/search.store'

const searchStore = useSearchStore()
</script>
