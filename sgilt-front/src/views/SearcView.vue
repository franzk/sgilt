<template>
  <section class="search-view">
    <div class="search-bar">
      <p>SEARCH</p>
    </div>
    <div class="search-results">
      <p>RESULTS</p>
    </div>
  </section>
</template>

<script setup lang="ts"></script>

<style scoped lang="scss">
.search-view {
  display: flex;
  flex-direction: row;

  .search-bar {
    flex: 0 0 20rem;
    background-color: $shadow;
    padding: $spacing-m;
  }

  .search-results {
    flex: 1;
    background-color: $color-secondary;
    padding: $spacing-m;
  }
}
</style>
