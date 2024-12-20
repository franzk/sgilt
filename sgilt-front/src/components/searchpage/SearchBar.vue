<template>
  <div class="filters">
    <div class="filters-title">Rechercher</div>
    <DateFilter class="filters-date" />
    <CategoryTagsFilter />
  </div>
</template>

<script setup lang="ts">
import DateFilter from '@/components/searchpage/DateFilter.vue'
import CategoryTagsFilter from '@/components/searchpage/CategoryTagsFilter.vue'
</script>

<style scoped lang="scss">
.filters {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}
</style>
