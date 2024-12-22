<template>
  <div class="filters">
    <div class="filters-title">Rechercher</div>
    <DateFilter class="filters-date" />
    <PriceFilter />
    <CategoriesFilter />
  </div>
</template>

<script setup lang="ts">
import DateFilter from '@/components/searchpage/DateFilter.vue'
import CategoriesFilter from '@/components/searchpage/CategoriesFilter.vue'
import PriceFilter from '@/components/searchpage/PriceFilter.vue'
</script>

<style scoped lang="scss">
.filters {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}
</style>
