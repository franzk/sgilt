<template>
  <div class="search-view">
    <aside class="search-bar">
      <SearchBar @search="search" />
    </aside>
    <main class="search-results">
      <SearchResults />
    </main>
  </div>
</template>

<script setup lang="ts">
import SearchBar from '@/components/search/SearchBar.vue'
import SearchResults from '@/components/search/SearchResults.vue'
import { useSearchStore } from '@/stores/search.store'
import type { PartnerQuery } from '@/types/PartnerQuery'

const searchStore = useSearchStore()

// initial search
const query = {}
searchStore.search(query)

// handle search event
const search = (query: PartnerQuery) => {
  searchStore.search(query)
}
</script>

<style scoped lang="scss">
.search-view {
  flex: 1;

  display: flex;
  flex-direction: row;
  overflow: hidden;

  .search-bar {
    flex: 0 0 21rem;
    border-right: $border-width-s solid $color-accent;
    display: flex;
    overflow: hidden;
  }

  .search-results {
    flex: 1;
    background-color: $color-secondary;
    padding: $spacing-m;
    overflow: auto;
  }
}
</style>
