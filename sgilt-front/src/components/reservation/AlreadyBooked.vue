<template>
  <!-- if booked -->
  <div>
    <p class="booked-title">{{ $t('reservation.form.booked.title', { partnerName }) }}</p>
    <p>{{ $t('reservation.form.booked.subtitle') }}</p>
  </div>

  <div class="related-partners">
    <div class="partners">
      <PartnerItem
        v-for="partner in relatedPartners"
        :key="partner.id"
        :partner="partner"
        @click="selectRelatedPartner(partner)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { getReleatedPartners } from '@/data/services/PartnerService'
import type { Partner } from '@/data/domain/Partner'
import PartnerItem from '@/components/partner/PartnerItem.vue'
import router from '@/router'

const props = defineProps<{
  partnerName: string
  partnerId: string
  reservationDate?: Date
}>()

const relatedPartners = ref<Partner[]>([])

onMounted(() => {
  fetchRelatedPartners(props.partnerId)
})

watch(
  () => props.partnerId,
  (newId) => {
    fetchRelatedPartners(newId)
  },
)

const fetchRelatedPartners = (partnerId: string) => {
  getReleatedPartners(partnerId).then((partners) => {
    relatedPartners.value = partners
  })
}

const selectRelatedPartner = (partner: Partner) => {
  // redirect to partner page
  router.push(`/${partner.slug}`)
}
</script>

<style scoped lang="scss">
.booked-title {
  font-weight: bold;
}
</style>
