<template>
  <div class="search-page">
    <section class="search-header">
      <SgiltDateFilter v-model="dateFilter" />
      <!--SgiltCategoryFilter v-model="categoryFilter" />
      <SgiltSubCategoryFilter v-model="subCategoryFilter" :category="categoryFilter" /-->
    </section>

    <section class="search-results">
      <!--SgiltSearchResults
        :dateFilter="dateFilter"
        :categoryFilter="categoryFilter"
        :subCategoryFilter="subCategoryFilter"
      /-->
    </section>
  </div>
</template>

<script setup lang="ts">
import SgiltDateFilter from '~/components/composed/SgiltDateFilter.vue'

const dateFilter = ref<Date | null>(null)
</script>

<style scoped lang="scss">
.search-results {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
}

.search-header {
  display: flex;
  flex-direction: column;
  width: 100%;
}
</style>
