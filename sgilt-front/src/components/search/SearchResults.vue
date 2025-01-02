<template>
  <div v-if="mobileView" @click="$emit('open-filter')" class="filter-button">
    <SgiltSimpleButton>{{ `${$t('texts.filtres')}${filtersCount}` }}</SgiltSimpleButton>
  </div>
  <div class="results-count">
    {{ $t('texts.resultats', { count: searchStore.results.length }) }}
  </div>
  <PartnerGrid :partners="searchStore.results" />
</template>

<script setup lang="ts">
import PartnerGrid from '@/components/partner/PartnerGrid.vue'
import { useSearchStore } from '@/stores/search.store'
import { mobileView } from '@/utils/StyleUtils'
import SgiltSimpleButton from '@/components/basics/buttons/SgiltSimpleButton.vue'
import { computed } from 'vue'

const searchStore = useSearchStore()

const filtersCount = computed(() =>
  searchStore.filtersCount ? ` (${searchStore.filtersCount})` : '',
)

defineEmits<{
  'open-filter': void
}>()
</script>

<style lang="scss" scoped>
.results-count {
  margin: $spacing-m 0;
  font-style: italic;
}
.filter-button {
  button {
    width: 100%;
  }
}
</style>
