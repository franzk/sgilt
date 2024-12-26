<template>
  <div class="search-view">
    <aside class="search-bar">
      <SearchBar />
    </aside>
    <main class="search-results">
      <p>RESULTS: !!!! régler les problèmes de outline</p>
    </main>
  </div>
</template>

<script setup lang="ts">
import SearchBar from '@/components/searchpage/SearchBar.vue'
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
  }
}
</style>
