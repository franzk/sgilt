<template>
  <div class="search-view">
    <aside v-show="searchBarOpened" class="search-bar">
      <SearchBar
        @search="search"
        @close="searchBarOpened = false"
        :dateFilter="dateFilter"
        :tagsFilter="tagsFilter"
      />
    </aside>
    <aside v-if="!searchBarOpened" class="reduced-search-bar">
      <div @click="searchBarOpened = true">&gt;&gt;</div>
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

const searchBarOpened = ref(true)

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

  aside {
    border-right: $border-width-s solid $color-accent;
  }

  .search-bar {
    flex: 0 0 21rem;
    display: flex;
    overflow: hidden;
  }

  .reduced-search-bar {
    display: flex;
    justify-content: center;
    background-color: $color-secondary;
    cursor: pointer;
    padding: $spacing-ml $spacing-m;
    div {
      height: $spacing-m;
      padding-bottom: $spacing-m;
      border-bottom: $border-width-s solid $color-accent;
    }
  }

  .search-results {
    flex: 1;
    background-color: $color-secondary;
    padding: $spacing-m;
    overflow: auto;
  }
}
</style>
