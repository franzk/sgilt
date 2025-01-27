<template>
  <div class="search-results">
    <div v-for="partner in partners" :key="partner.id">
      <PartnerCard :partner="partner" />
    </div>
  </div>
</template>

<script setup lang="ts">
import PartnerCard from '@/components/partner/PartnerCard.vue'
import type { PartnerSearchViewModel } from '@/data/domain/viewmodels/PartnerSearchViewModel'

defineProps<{
  partners: PartnerSearchViewModel[]
}>()
</script>

<style scoped lang="scss">
.search-results {
  display: grid;
  gap: 1rem;
  padding: 1rem;
  grid-template-columns: repeat(auto-fill, minmax(15rem, 1fr));

  // center if there is only one column
  justify-content: center;
}
</style>
