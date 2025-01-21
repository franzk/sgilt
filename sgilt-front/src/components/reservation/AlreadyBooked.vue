<template>
  <!-- if booked -->
  <div>
    <p class="booked-title">{{ $t('reservation.form.booked.title', { partnerName }) }}</p>
    <p>{{ $t('reservation.form.booked.subtitle') }}</p>
  </div>

  <div class="related-partners">
    <div class="partners">
      <PartnerTail v-for="partner in relatedPartners" :key="partner.id" :partner="partner" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getReleatedPartners } from '@/data/services/PartnerService'
import type { Partner } from '@/data/domain/Partner'
import PartnerTail from '@/components/partner/PartnerTail.vue'

const props = defineProps<{
  partnerName: string
  partnerId: string
}>()

const relatedPartners = ref<Partner[]>([])

onMounted(() => {
  // fetch related partners
  getReleatedPartners(props.partnerId).then((partners) => {
    relatedPartners.value = partners
  })
})
</script>

<style scoped lang="scss">
.booked-title {
  font-weight: bold;
}
</style>
