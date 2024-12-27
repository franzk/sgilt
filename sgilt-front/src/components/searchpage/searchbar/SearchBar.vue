<template>
  <div class="search-bar-wrapper">
    <div class="filters">
      <div class="bar-title">
        <div class="filters-title">
          <div class="icon"><IconFilters /></div>
          <p>{{ $t('texts.filtres') }}</p>
        </div>
        <hr />
      </div>
      <DateFilter class="filters-date" />
      <PriceFilter class="price-filter" />
      <CategoriesFilter class="categories-filter" />
    </div>
    <div class="submit-button">
      <SgiltSimpleButton>{{ $t('texts.rechercher') }}</SgiltSimpleButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import DateFilter from '@/components/searchpage/searchbar/filters/DateFilter.vue'
import CategoriesFilter from '@/components/searchpage/searchbar/filters/CategoriesFilter.vue'
import PriceFilter from '@/components/searchpage/searchbar/filters/PriceFilter.vue'
import SgiltSimpleButton from '@/components/basics/buttons/SgiltSimpleButton.vue'
import IconFilters from '@/components/icons/IconFilters.vue'
</script>

<style scoped lang="scss">
.search-bar-wrapper {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: white;
}

.filters {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m;
  overflow: scroll;
}

.filters-title {
  display: flex;
  flex-direction: row;
  margin: 0;
  padding: 0;
  line-height: 0;
  align-items: center;
  gap: $spacing-s;
  .icon {
    height: 24px;
    width: 24px;
  }
  p {
    font-weight: bold;
  }
}

hr {
  border: none;
  border-top: $border-width-s solid $color-accent;
  width: 100%;
}

.submit-button {
  padding: $spacing-m;
  button {
    width: 100%;
  }
}
</style>
