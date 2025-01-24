<template>
  <div class="form-group">
    <!-- title -->
    <p>{{ $t('reservation.form.options-label') }}</p>

    <!-- select options -->
    <SgiltSelect :options="pricesOptions || []" v-model="selectedOption">
      <template v-slot:left-icon>
        <IconList />
      </template>
    </SgiltSelect>

    <!-- error message -->
    <p class="error-msg">{{ reservationStore.priceError }}&nbsp;</p>
  </div>

  <!-- Price estimation -->
  <div class="pricing">
    <p>{{ $t('reservation.form.pricing') }}</p>
    <p class="price">{{ calculatedPrice }} €</p>
  </div>
</template>

<script setup lang="ts">
import { usePartnerStore } from '@/stores/partner.store'
import { useReservationStore } from '@/stores/reservation.store'
import { computed, ref, watch } from 'vue'
import SgiltSelect from '@/components/basics/inputs/SgiltSelect.vue'
import type { SgiltSelectOption } from '@/components/basics/inputs/SgiltSelect.vue'
import IconList from '@/components/icons/IconList.vue'
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

// stores
const partnerStore = usePartnerStore()
const partner = computed(() => partnerStore.partner)

const reservationStore = useReservationStore()

// list of prices options
const pricesOptions = computed(() =>
  [{ value: '-1', label: t('reservation.form.price-placeholder') }].concat(
    partner.value?.prices?.map((price) => ({
      value: price.id,
      label: price.title,
    })) || [],
  ),
)
const selectedOption = ref<SgiltSelectOption>()

// selected price
const selectedPrice = computed(() =>
  partner.value?.prices?.find((price) => price.id === selectedOption.value?.value),
)

watch(
  () => selectedPrice.value,
  (newValue) => {
    reservationStore.price = newValue
    reservationStore.priceError = ''
  },
)

// calculated price
const calculatedPrice = computed(() => {
  return selectedPrice.value?.price || 0
})

// reset selected option when prices change
watch(
  () => partner.value?.prices,
  () => {
    selectedOption.value = pricesOptions.value?.[0]
    reservationStore.price = undefined
    reservationStore.priceError = ''
  },
)
</script>

<style scoped lang="scss">
.pricing {
  text-align: left;
  margin-bottom: $spacing-m;

  .price {
    font-size: 24px;
    color: $color-accent;
    font-weight: bold;
  }
}
</style>
