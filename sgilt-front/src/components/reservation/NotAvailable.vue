<template>
  <!-- if booked -->
  <div v-if="partner">
    <p class="booked-title">
      {{ $t(`reservation.form.${state}.title`, { partnerName: partner.title }) }}
    </p>
    <p class="waiting-list">
      <SgiltButton>S’inscrire sur la liste d’attente pour cette date</SgiltButton>
    </p>
    <p>{{ $t(`reservation.form.${state}.subtitle`) }}</p>
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
import { computed, onMounted, ref, watch } from 'vue'
import { getReleatedPartners } from '@/data/services/PartnerService'
import type { Partner } from '@/data/domain/Partner'
import PartnerItem from '@/components/partner/PartnerItem.vue'
import router from '@/router'
import { usePartnerStore } from '@/stores/partner.store'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'

defineProps<{
  state: string
}>()

const partnerStore = usePartnerStore()
const partner = computed(() => partnerStore.partner)

const relatedPartners = ref<Partner[]>([])

onMounted(() => {
  if (partner.value?.id) {
    fetchRelatedPartners(partner.value.id)
  }
})

watch(
  () => partner.value?.id,
  (newId) => {
    fetchRelatedPartners(newId)
  },
)

const fetchRelatedPartners = (partnerId: string | undefined) => {
  if (!partnerId) {
    relatedPartners.value = []
  } else {
    getReleatedPartners(partnerId).then((partners) => {
      relatedPartners.value = partners
    })
  }
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

.waiting-list {
  text-align: center;
}

.related-partners {
  margin-top: $spacing-l;
}
</style>
