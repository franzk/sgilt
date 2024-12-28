<template>
  <div class="search-view">
    <aside class="search-bar">
      <SearchBar @search="search" :dateFilter="dateFilter" />
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
import dayjs from 'dayjs'
import { useRoute } from 'vue-router'

const searchStore = useSearchStore()

// get params from route
const route = useRoute()

const dateFilter = route.query?.date ? dayjs(route.query.date as string).toDate() : undefined
const eventType = (route.query.event || '') as string
console.log('eventType', eventType)

// initial search
const searchQuery: PartnerQuery = {}
if (dateFilter) searchQuery.dateFilter = dateFilter
searchStore.search(searchQuery)

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
