<template>
  <div class="search-view">
    <aside class="search-bar">
      <SearchBar @search="search" :dateFilter="dateFilter" :tagsFilter="tagsFilter" />
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
import { useTagsStore } from '@/stores/tag.store'
import type { PartnerQuery } from '@/types/PartnerQuery'
import type { TagFilter } from '@/types/TagFilter'
import dayjs from 'dayjs'
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const searchStore = useSearchStore()

// get params from route
const route = useRoute()
const dateFilter = route.query?.date ? dayjs(route.query.date as string).toDate() : undefined
// TODO : eventType param
const category = (route.query?.category || '') as string
const tagsFilter = ref<TagFilter[]>(
  useTagsStore()
    .tags.filter((tag) => tag.category === category)
    .map((tag) => ({ id: tag.id, name: tag.name, category: tag.category })),
)

// filters  herited from route
const searchQuery: PartnerQuery = {}
if (dateFilter) {
  searchQuery.dateFilter = dateFilter
}
if (tagsFilter.value.length > 0) {
  searchQuery.tagsId = tagsFilter.value.map((tag) => tag.id)
}

// initial search with route params
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
