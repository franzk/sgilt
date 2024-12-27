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
      <PriceFilter v-model:min-price="minPrice" v-model:max-price="maxPrice" class="price-filter" />
      <CategoriesFilter v-model="categoriesFilter" class="categories-filter" />
    </div>
    <div class="submit-button">
      <SgiltSimpleButton @click="search">{{ $t('texts.rechercher') }}</SgiltSimpleButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import DateFilter from '@/components/search/filters/DateFilter.vue'
import CategoriesFilter from '@/components/search/filters/CategoriesFilter.vue'
import PriceFilter from '@/components/search/filters/PriceFilter.vue'
import SgiltSimpleButton from '@/components/basics/buttons/SgiltSimpleButton.vue'
import IconFilters from '@/components/icons/IconFilters.vue'
import type { PartnerQuery } from '@/types/PartnerQuery'
import type { CategoryFilter } from '@/types/CategoryFilter'

const minPrice = ref(0)
const maxPrice = ref(0)
const categoriesFilter = ref<CategoryFilter[]>([])

const emit = defineEmits<{
  search: [query: PartnerQuery]
}>()

const search = () => {
  console.log('search')
  const query: PartnerQuery = {
    dateFilter: DateFilter.date,
    minPrice: minPrice.value,
    maxPrice: maxPrice.value,
    categoryTags: categoriesFilter.value.flatMap((category) => category.selection),
  }
  emit('search', query)
}
</script>

<style scoped lang="scss">
.search-bar-wrapper {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: $color-white;
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
