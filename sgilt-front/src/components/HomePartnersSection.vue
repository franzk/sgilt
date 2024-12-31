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
import type { Partner } from '@/domain/Partner'
import { PartnerService } from '@/services/PartnerService'
import { getCssVariable } from '@/utils/StyleUtils'

const mobileBreakpoint = parseInt(getCssVariable('--breakpoint-mobile'))
const mobileView = window.matchMedia(`(max-width: ${mobileBreakpoint}px)`).matches

const partners: Partner[] = PartnerService.getHihglightedPartners(mobileView ? 2 : 3)
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
