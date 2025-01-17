<template>
  <div class="reservation-form">
    <!-- Header Section -->
    <div class="reservation-header">
      <h2>{{ $t('reservation.title') }}</h2>
      <p class="subtitle">{{ $t('reservation.subtitle') }}</p>
    </div>

    <!-- Body Section -->
    <div class="reservation-body">
      <!-- Datepicker -->
      <div class="form-group">
        <p>{{ $t('reservation.form.date-label') }}</p>
        <SgiltDatePicker v-model="selectedDate" />
        <p class="error-msg">{{ dateError }}&nbsp;</p>
      </div>

      <!-- Select options -->
      <div class="form-group">
        <p>{{ $t('reservation.form.options-label') }}</p>
        <SgiltSelect :options="pricesOptions" v-model="selectedOption">
          <template v-slot:left-icon>
            <IconList />
          </template>
        </SgiltSelect>
      </div>

      <!-- Pricing -->
      <div class="pricing">
        <p>{{ $t('reservation.form.pricing') }}</p>
        <p class="price">{{ calculatedPrice }} €</p>
      </div>
    </div>

    <!-- Footer Section -->
    <div class="reservation-footer">
      <SgiltButton @click="handleBooking" class="button">
        {{ $t('reservation.button') }}
      </SgiltButton>
      <div class="contact">
        {{ $t('reservation.help.need') }}&nbsp;<u>{{ $t('reservation.help.contact') }}</u>
      </div>
    </div>
  </div>

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

const { t } = useI18n()

const selectedDate = defineModel<Date>('selected-date')
const selectedPrice = defineModel<Price>('selected-price')
const showFirstReservationModal = ref<boolean>(false)

const props = defineProps<{
  prices: Price[]
}>()

// -- date --
const dateError = ref<string>('')
watch(
  () => selectedDate.value,
  (newValue) => {
    if (newValue) dateError.value = ''
  },
)

// -- price --
const pricesOptions = props.prices.map((price) => ({
  value: price.id,
  label: price.title,
}))

const selectedOption = ref<SgiltSelectOption>()
selectedPrice.value = props.prices?.[0] || {}

watch(
  () => selectedOption.value,
  (newValue) => {
    selectedPrice.value = props.prices.find((price) => price.id === newValue?.value)
  },
)

const calculatedPrice = computed(() => {
  return selectedPrice.value?.price || 0
})

// -- booking --
const handleBooking = () => {
  if (!selectedDate.value) {
    dateError.value = t('reservation.form.date-error')
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
  .reservation-header {
    h2 {
      font-size: $font-size-h1;
    }
  }
}
</style>
