<template>
  <div>{{ searchResults.length }} résultat(s)</div>
  <PartnerGrid :partners="searchResults" />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import PartnerGrid from '@/components/partner/PartnerGrid.vue'
import { useSearchStore } from '@/stores/search.store'

const searchStore = useSearchStore()

const searchResults = ref(searchStore.results)

searchStore.search({})
</script>
