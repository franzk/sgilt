<template>
  <div class="search-bar-wrapper">
    <div class="filters">
      <div class="bar-title">
        <div class="filters-title">
          <div class="icon"><IconFilters /></div>
          <p>{{ $t('texts.filtres') }}</p>
          <span @click="$emit('close')">{{ reduceOrCloseSymbol }}</span>
        </div>
        <hr />
      </div>
      <DateFilter class="filters-date" v-model="dateFilter" @update:model-value="search" />
      <PriceFilter
        v-model:min-price="minPrice"
        v-model:max-price="maxPrice"
        class="price-filter"
        @blur="search"
      />
      <TagsFilter v-model="tagsFilter" class="tags-filter" @update:model-value="search" />
    </div>
    <div class="submit-button">
      <span class="reset small-font" @click="resetFilters">Réinitialisez les filtres</span>
      <SgiltSimpleButton v-if="mobileView" @click="submit">{{
        $t('texts.voir-resultats', { count: useSearchStore().results.length })
      }}</SgiltSimpleButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import DateFilter from '@/components/search/filters/DateFilter.vue'
import TagsFilter from '@/components/search/filters/TagsFilter.vue'
import PriceFilter from '@/components/search/filters/PriceFilter.vue'
import SgiltSimpleButton from '@/components/basics/buttons/SgiltSimpleButton.vue'
import IconFilters from '@/components/icons/IconFilters.vue'
import type { PartnerQuery } from '@/types/PartnerQuery'
import type { TagFilter } from '@/types/TagFilter'
import { mobileView } from '@/utils/StyleUtils'
import { useSearchStore } from '@/stores/search.store'

// props
const props = defineProps<{
  dateFilter?: Date
  tagsFilter?: TagFilter[]
}>()

// emits
const emit = defineEmits<{
  search: [query: PartnerQuery]
  close: void
}>()

// filters
const dateFilter = ref(props.dateFilter)
const minPrice = ref(0)
const maxPrice = ref(0)
const tagsFilter = ref<TagFilter[]>(props.tagsFilter ?? [])

// computed
const reduceOrCloseSymbol = computed(() => (mobileView ? '✕' : '<<'))

// methods
const search = () => {
  const query: PartnerQuery = {
    dateFilter: dateFilter.value,
    minPrice: minPrice.value,
    maxPrice: maxPrice.value,
    tagsId: tagsFilter.value.map((tag) => tag.id),
  }
  emit('search', query)
}

const resetFilters = () => {
  dateFilter.value = undefined
  minPrice.value = 0
  maxPrice.value = 0
  tagsFilter.value = []
  search()
}

const submit = () => {
  if (minPrice.value && maxPrice.value && minPrice.value > maxPrice.value) {
    const temp = minPrice.value
    minPrice.value = maxPrice.value
    maxPrice.value = temp
  }
  search()
  emit('close')
}
</script>

<style scoped lang="scss">
.search-bar-wrapper {
  display: flex;
  width: 100%;
  flex-direction: column;
  overflow: hidden;
  background-color: $color-white;
  @include respond-to(mobile) {
    .filters-date,
    .price-filter {
      display: flex;
      justify-content: center;
    }
  }
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
  justify-content: space-between;
  gap: $spacing-s;

  .icon {
    height: 24px;
    width: 24px;
  }

  p {
    font-weight: bold;
    flex: 1;
  }

  span {
    cursor: pointer;
  }
}

hr {
  border: none;
  border-top: $border-width-s solid $color-accent;
  width: 100%;
}

.submit-button {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m;
  span {
    text-align: center;
    text-decoration: underline;
    cursor: pointer;
  }
  button {
    line-height: calc($line-height-base * 2);
    width: 100%;
  }
}
</style>
