<template>
  <section class="partners-section-body">
    <h2>{{ $t('home.partners-section.title') }}</h2>

    <div class="highligthed-partners-list">
      <PartnerCard
        v-for="partner in partners"
        :key="partner.id"
        :partner="partner"
        class="partner-card"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import PartnerCard from '@/components/partner/PartnerCard.vue'
import type { Partner } from '@/data/domain/Partner'
import { getHihglightedPartners } from '@/data/services/PartnerService'
import { mobileView } from '@/utils/StyleUtils'
import { ref } from 'vue'

const partners = ref<Partner[]>()

getHihglightedPartners(mobileView ? 2 : 3).then((p) => {
  partners.value = p
})
</script>

<style lang="scss" scoped>
.partners-section-body {
  display: flex;
  flex-direction: column;
  background-color: $color-secondary;
  color: $color-primary;
  align-items: center;
  h2 {
    margin: $spacing-l 0 $spacing-l 0;
    text-align: center;
  }
}

.highligthed-partners-list {
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
  width: 100%;

  .partner-card {
    width: 14rem;
  }

  @include respond-to(mobile) {
    padding: 0 $spacing-m;
    gap: $spacing-s;
    width: initial;
    .partner-card {
      width: 100%;
    }
  }
}
</style>
