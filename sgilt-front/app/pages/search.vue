<template>
  <div class="search-page">
    <section class="search-header">
      <SgiltDateFilter v-model="date" />

      <SgiltCategoryFilter v-model="categoryId" />

      <SgiltSubCategoryFilter
        v-if="categoryId !== '1'"
        :categoryId="categoryId"
        :activeSubcats="subcatsByCat[categoryId] || []"
        :counts="subcatCounts"
        @toggle="toggleSubcat"
      />
    </section>

    <section
      class="search-results margin-for-header"
      :class="{ 'margin-for-header-and-subcats': categoryId !== '1' }"
    >
      <SgiltSearchResults :results="results" :loading="loading" :error="error" />
    </section>
  </div>
</template>

<script setup lang="ts">
import SgiltDateFilter from '~/components/composed/SgiltDateFilter.vue'
import SgiltCategoryFilter from '~/components/composed/SgiltCategoryFilter.vue'
import SgiltSubCategoryFilter from '~/components/composed/SgiltSubCategoryFilter.vue'
import SgiltSearchResults from '~/components/composed/SgiltSearchResults.vue'

// On récupère l'état de l'UI
const { date, categoryId, subcatsByCat, toggleSubcat } = useSearchUi()

// On récupère les données (le watcher interne lance le fetch auto)
const { results, loading, countsByCategory, subcatCounts, error } = useSearchFetch()
</script>

<style scoped lang="scss">
.search-page {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
}

.search-results {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
}

.margin-for-header {
  margin-top: 140px; // Laisse de la place pour le header fixe
}

.margin-for-header-and-subcats {
  margin-top: 200px; // Laisse de la place pour le header fixe + filtre sous catégorie
}

.search-header {
  display: flex;
  flex-direction: column;
  width: 100%;
  position: fixed;
  z-index: 10;
  background-color: #ffffff;
}
</style>
