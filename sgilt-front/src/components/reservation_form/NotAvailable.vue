<template>
  <!-- if booked -->
  <div v-if="partner">
    <p class="booked-title">
      {{ $t(`reservation.form.${state}.title`, { partnerName: partner.title }) }}
    </p>
    <p class="waiting-list" v-if="state === 'option'">
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
        selectable
        @click="selectRelatedPartner(partner.slug)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { getReleatedPartners } from '@/data/services/PartnerService'
import PartnerItem from '@/components/partner/PartnerItem.vue'
import router from '@/router'
import { usePartnerStore } from '@/stores/partner.store'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'
import type { PartnerSearchViewModel } from '@/data/domain/viewmodels/PartnerSearchViewModel'
import { useReservationStore } from '@/stores/reservation.store'

defineProps<{
  state: string
}>()

const partnerStore = usePartnerStore()
const partner = computed(() => partnerStore.partner)

const reservationStore = useReservationStore()

const relatedPartners = ref<PartnerSearchViewModel[]>([])

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
    getReleatedPartners(partnerId, reservationStore.dateReservation).then((partners) => {
      relatedPartners.value = partners
    })
  }
}

const selectRelatedPartner = (partnerSlug: string) => {
  // redirect to partner page
  router.push(`/${partnerSlug}`)
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
  .partners {
    display: grid;
    gap: $spacing-m;
  }
}
</style>
