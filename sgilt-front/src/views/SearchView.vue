<template>
  <div class="search-view">
    <aside v-show="searchBarOpened" class="search-bar">
      <SearchBar @search="search" @close="searchBarOpened = false" :tagsFilter="tagsFilter" />
    </aside>
    <aside v-if="!searchBarOpened && !mobileView" class="reduced-search-bar">
      <div
        @click="searchBarOpened = true"
        @keydown.space="searchBarOpened = true"
        @keydown.enter="searchBarOpened = true"
        tabindex="0"
        class="open-button"
      >
        &gt;&gt;
      </div>
      <div class="separator"></div>
    </aside>
    <main v-if="!mobileView || !searchBarOpened" class="search-results">
      <SearchResults @open-filter="searchBarOpened = true" />
    </main>
  </div>
</template>

<script setup lang="ts">
import SearchBar from '@/components/search/SearchBar.vue'
import SearchResults from '@/components/search/SearchResults.vue'
import { useSearchStore } from '@/stores/search.store'
import { useTagsStore } from '@/stores/tag.store'
import type { PartnerQuery } from '@/data/api/query/PartnerQuery'
import type { TagFilter } from '@/types/TagFilter'
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { mobileView } from '@/utils/StyleUtils'
import { useReservationStore } from '@/stores/reservation.store'

const searchStore = useSearchStore()
const reservationStore = useReservationStore()

const searchBarOpened = ref(true)

// get params from route
const route = useRoute()
const category = (route.query?.category || '') as string
const tagsFilter = ref<TagFilter[]>(
  useTagsStore()
    .tags.filter((tag) => tag.category === category)
    .map((tag) => ({ id: tag.id, name: tag.name, category: tag.category })),
)

// date filter from reservation store
const searchQuery: PartnerQuery = {}
if (reservationStore.dateReservation) {
  searchQuery.dateFilter = reservationStore.dateReservation
}

// set tags filter from route
if (tagsFilter.value.length > 0) {
  searchQuery.tagsId = tagsFilter.value.map((tag) => tag.id)
  searchBarOpened.value = !mobileView // close search bar on mobile if filters are set in route
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
    @include respond-to(mobile) {
      flex: 1;
      overflow: initial;
    }
  }

  .reduced-search-bar {
    display: flex;
    flex-direction: column;
    justify-content: center;
    background-color: $color-secondary;
    cursor: pointer;
    padding: $spacing-ml $spacing-m;
    .open-button {
      height: $spacing-m;
      margin-bottom: $spacing-m;
    }
    .separator {
      flex: 1;
      border-top: $border-width-s solid $color-accent;
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
