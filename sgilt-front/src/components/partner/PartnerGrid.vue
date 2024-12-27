<template>
  {{ searchResults.length }} résultats
  <div class="search-results">
    <div v-for="result in searchResults" :key="result.id" class="thumbnail">
      <PartnerThumbnail :partner="result" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useSearchStore } from '@/stores/search.store'
import { onMounted, ref } from 'vue'
import PartnerThumbnail from './PartnerThumbnail.vue'

const searchStore = useSearchStore()

const searchResults = ref(searchStore.results)

onMounted(() => {
  searchStore.search({})
})
</script>

<style scoped lang="scss">
.search-results {
  display: grid;
  gap: 1rem;
  padding: 1rem;
  grid-template-columns: repeat(auto-fill, minmax(15rem, 1fr));

  // Centrer les éléments si moins de colonnes
  justify-content: center;
}
</style>
