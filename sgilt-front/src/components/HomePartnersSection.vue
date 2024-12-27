<template>
  <section class="partners-section-body">
    <h2>{{ $t('home.partners-section.title') }}</h2>

    <div class="highligthed-partners-list">
      <PartnerThumbnail v-for="partner in partners" :key="partner.id" :partner="partner" />
    </div>
  </section>
</template>

<script setup lang="ts">
import PartnerThumbnail from '@/components/partner/PartnerThumbnail.vue'
import type { Partner } from '@/domain/Partner'
import { PartnerService } from '@/services/PartnerService'

const partners: Partner[] = PartnerService.getHihglightedPartners()
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
  }
}

.highligthed-partners-list {
  flex: 1;
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-evenly;
  .partner-thumbnail {
    flex-basis: 25%;
  }
}
</style>
