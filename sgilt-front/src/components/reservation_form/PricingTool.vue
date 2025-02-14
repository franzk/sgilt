<template>
  <!-- Options -->
  <SgiltFormGroup
    :title="$t('reservation.form.options-label')"
    :errorMessage="reservationStore.priceError"
  >
    <SgiltSelect :options="pricesOptions || []" v-model="selectedOption" focusable>
      <template v-slot:left-icon>
        <IconList />
      </template>
    </SgiltSelect>
  </SgiltFormGroup>

  <!-- Quantity -->
  <SgiltFormGroup
    v-if="reservationStore.price?.unity"
    :title="$t('reservation.form.quantity-label')"
    :errorMessage="reservationStore.quantityError"
  >
    <div class="quantity">
      <SgiltInput type="number" v-model:number-model="reservationStore.quantity" />
      <p>{{ reservationStore.price?.unity }}</p>
    </div>
  </SgiltFormGroup>

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
import SgiltInput from '@/components/basics/inputs/SgiltInput.vue'
import SgiltFormGroup from '@/components/basics/inputs/SgiltFormGroup.vue'
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

// on new price selection
watch(
  () => selectedPrice.value,
  (newValue) => {
    reservationStore.price = newValue
    reservationStore.priceError = ''
    if (newValue?.unity && !reservationStore.quantity) {
      reservationStore.quantity = newValue?.minQuantity || 0
    }
  },
)

// calculated price
const calculatedPrice = computed(() => {
  if (!selectedPrice.value?.unity) {
    return selectedPrice.value?.price || 0
  } else {
    return (selectedPrice.value?.price || 0) * (reservationStore.quantity || 0)
  }
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
.quantity {
  display: flex;
  gap: $spacing-s;
}
</style>
