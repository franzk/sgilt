<template>
  <section class="search-view">
    <aside class="search-bar">
      <SearchBar />
    </aside>
    <div class="search-results">
      <p>RESULTS</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import SearchBar from '@/components/searchpage/SearchBar.vue'
</script>

<style scoped lang="scss">
.search-view {
  flex: 1;

  display: flex;
  flex-direction: row;

  .search-bar {
    flex: 0 0 20rem;
    background-color: $ghost;
    padding: $spacing-m;
  }

  .search-results {
    flex: 1;
    background-color: $color-secondary;
    padding: $spacing-m;
  }
}
</style>
