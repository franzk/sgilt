<template>
  <div class="reservation-form" v-if="partner">
    <!-- Header Section : title & subtitle -->
    <div class="reservation-header">
      <h2>{{ $t('reservation.title') }}</h2>
      <p class="subtitle">{{ $t('reservation.subtitle') }}</p>
    </div>

    <!-- Body Section -->
    <div class="reservation-body">
      <!-- Datepicker -->
      <div class="form-group">
        <p>{{ $t('reservation.form.date-label') }}</p>
        <SgiltDatePicker
          v-model="selectedDate"
          :booked-dates="bookedDates"
          :option-dates="optionDates"
        />
        <p class="error-msg">{{ dateError }}&nbsp;</p>
      </div>

      <!-- Select options -->
      <div class="form-group" v-if="chosenDateState !== 'booked'">
        <p>{{ $t('reservation.form.options-label') }}</p>
        <SgiltSelect :options="pricesOptions || []" v-model="selectedOption">
          <template v-slot:left-icon>
            <IconList />
          </template>
        </SgiltSelect>
        <p class="error-msg">{{ priceError }}&nbsp;</p>
      </div>

      <!-- Pricing -->
      <div class="pricing" v-if="chosenDateState !== 'booked'">
        <p>{{ $t('reservation.form.pricing') }}</p>
        <p class="price">{{ calculatedPrice }} €</p>
      </div>
    </div>

    <!-- if booked -->
    <AlreadyBooked v-if="chosenDateState === 'booked'" :reservationDate="selectedDate" />

    <!-- Footer Section submit & help -->
    <div class="reservation-footer">
      <SgiltButton @click="handleBooking" class="button" v-if="chosenDateState !== 'booked'">
        {{ $t('reservation.button') }}
      </SgiltButton>
      <div class="contact">
        {{ $t('reservation.help.need') }}&nbsp;<u>{{ $t('reservation.help.contact') }}</u>
      </div>
    </div>
  </div>

  <!-- First reservation modal in cas of submission -->
  <FirsReservationModal
    :showModal="showFirstReservationModal"
    @close="showFirstReservationModal = false"
    @connect-email="newUserConnected"
    @connect-s-s-o="newUserConnected"
  />
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import SgiltDatePicker from '@/components/basics/inputs/SgiltDatePicker.vue'
import SgiltSelect, { type SgiltSelectOption } from '@/components/basics/inputs/SgiltSelect.vue'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'
import FirsReservationModal from '@/components/event/FirstReservationModal.vue'
import IconList from '@/components/icons/IconList.vue'
import type { Price } from '@/data/domain/Partner'
import { useI18n } from 'vue-i18n'
import { dateArrayContains } from '@/utils/ArrayUtils'
import AlreadyBooked from '@/components/reservation/AlreadyBooked.vue'
import { usePartnerStore } from '@/stores/partner.store'
const { t } = useI18n()

const partnerStore = usePartnerStore()
const partner = computed(() => partnerStore.partner)

// v-models
const selectedDate = defineModel<Date>('selected-date')
const selectedPrice = defineModel<Price>('selected-price')

// -- date --
const chosenDateState = computed(() => {
  // is the selected date available, booked or option ?
  const date = selectedDate.value
  if (!date) return 'empty'
  if (bookedDates.value && dateArrayContains(bookedDates.value, date)) return 'booked'
  if (optionDates.value && dateArrayContains(optionDates.value, date)) return 'option'
  return 'available'
})

const dateError = ref<string>('') // reset error message when date changes
watch(
  () => selectedDate.value,
  (newValue) => {
    if (newValue) dateError.value = ''
  },
)

// -- price --
const priceError = ref<string>('') // reset error message when price changes

const pricesOptions = computed(() =>
  // list of prices options
  [{ value: '-1', label: t('reservation.form.price-placeholder') }].concat(
    partner.value?.prices?.map((price) => ({
      value: price.id,
      label: price.title,
    })) || [],
  ),
)

const selectedOption = ref<SgiltSelectOption>()
selectedPrice.value = partner.value?.prices?.[0] // default selected price

watch(
  // update selected price when selected option changes
  () => selectedOption.value,
  (newValue) => {
    priceError.value = ''
    selectedPrice.value = partner.value?.prices?.find((price) => price.id === newValue?.value)
  },
)

const calculatedPrice = computed(() => {
  return selectedPrice.value?.price || 0
})

watch(
  // reset selected option when prices change
  () => partner.value?.prices,
  () => {
    selectedOption.value = pricesOptions.value?.[0]
  },
)

// -- booking --
const bookedDates = computed(() =>
  partner.value?.calendar?.filter((entry) => entry.state === 'booked').map((entry) => entry.date),
)

const optionDates = computed(() =>
  partner.value?.calendar?.filter((entry) => entry.state === 'option').map((entry) => entry.date),
)

// submit button
const showFirstReservationModal = ref<boolean>(false)
const handleBooking = () => {
  if (!selectedDate.value) {
    dateError.value = t('reservation.form.date-error') // date is required
  } else if (selectedOption.value?.value === '-1') {
    priceError.value = t('reservation.form.price-error') // price is required
  } else {
    showFirstReservationModal.value = true // Open first reservation modal
  }
}

// -> back from first reservation modal
const newUserConnected = (email: string) => {
  console.log('New user connected with email:', email)
}
</script>

<style scoped lang="scss">
/* Main container */
.reservation-form {
  width: 20rem;
  background: $color-white;
  padding: $spacing-l;
  border-radius: $border-radius-s;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  margin: $spacing-m auto;

  h2 {
    color: $color-primary;
    margin: 0 0 $spacing-m 0;
  }

  /* Form groups */
  .form-group {
    margin-bottom: $spacing-l;

    .error-msg {
      color: $color-error;
      font-size: $font-size-base;
      margin-top: $spacing-s;
      font-weight: 500;
    }

    p {
      font-weight: bold;
      margin-bottom: $spacing-s;
      display: block;
    }
  }

  /* Pricing section */
  .pricing {
    text-align: left;
    margin-bottom: $spacing-m;

    .price {
      font-size: 24px;
      color: $color-accent;
      font-weight: bold;
    }
  }

  .reservation-footer {
    display: flex;
    flex-direction: column;
    align-items: center;
    .button {
      line-height: 1.5em;
    }
  }

  /* Contact link */
  .contact {
    margin-top: $spacing-m;
    color: $color-primary;
    cursor: pointer;
    display: flex;
    align-items: center;
    font-style: italic;
  }
}
/* Responsive */
@include respond-to(tablet) {
  .reservation-form {
    width: 30rem;
  }
  .reservation-header {
    h2 {
      font-size: $font-size-h1;
    }
  }
}
@include respond-to(mobile) {
  .reservation-form {
    width: initial;
  }
}
</style>
