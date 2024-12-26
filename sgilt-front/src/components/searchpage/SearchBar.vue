<template>
  <div class="search-bar-wrapper">
    <div class="filters">
      <div class="bar-title">
        <div class="filters-title">
          <div class="icon"><IconSearch /></div>
          <p>{{ $t('texts.rechercher') }}</p>
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
import DateFilter from '@/components/searchpage/DateFilter.vue'
import CategoriesFilter from '@/components/searchpage/CategoriesFilter.vue'
import PriceFilter from '@/components/searchpage/PriceFilter.vue'
import SgiltSimpleButton from '@/components/basics/buttons/SgiltSimpleButton.vue'
import IconSearch from '@/components/icons/IconSearch.vue'
</script>

<style scoped lang="scss">
.search-bar-wrapper {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.filters {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
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
}

hr {
  border: none;
  border-top: $border-width-s solid $color-accent;
  width: 100%;
}

.submit-button {
  // to come
}
</style>
