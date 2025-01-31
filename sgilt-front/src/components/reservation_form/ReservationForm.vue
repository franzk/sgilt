<template>
  <div class="reservation-form" v-if="partner">
    <!-- Header Section : title & subtitle -->
    <div class="reservation-header">
      <h2>{{ $t('reservation.title') }}</h2>
      <p class="subtitle">{{ $t('reservation.subtitle') }}</p>
    </div>

    <div class="reservation-body">
      <!-- Datepicker -->
      <SgiltFormGroup
        :title="$t('reservation.form.date-label')"
        :errorMessage="reservationStore.dateError"
      >
        <SgiltDatePicker
          v-model="reservationStore.dateReservation"
          :booked-dates="bookedDates"
          :option-dates="optionDates"
        />
      </SgiltFormGroup>

      <!-- Pricing -->
      <PricingTool v-if="dateIsAvailable" />
    </div>

    <!-- if not available -->
    <NotAvailable v-if="!dateIsAvailable" :state="chosenDateState" />

    <!-- Footer Section submit & help -->
    <div class="reservation-footer">
      <SgiltButton @click="handleBooking" class="button" v-if="dateIsAvailable">
        {{ $t('reservation.button') }}
      </SgiltButton>
      <div class="contact">
        {{ $t('reservation.help.need') }}&nbsp;<u>{{ $t('reservation.help.contact') }}</u>
      </div>
    </div>
  </div>

  <!-- First reservation modal in cas of submission -->
  <!--FirsReservationModal
    :showModal="showFirstReservationModal"
    @close="showFirstReservationModal = false"
    @connect-email="newUserConnected"
    @connect-s-s-o="newUserConnected"
  /-->
  <BookingFlow :showModal="showBookingFlow" @close="showBookingFlow = false" />
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import SgiltDatePicker from '@/components/basics/inputs/SgiltDatePicker.vue'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'
import { dateArrayContains } from '@/utils/ArrayUtils'
import { usePartnerStore } from '@/stores/partner.store'
import PricingTool from '@/components/reservation_form/PricingTool.vue'
import { useReservationStore } from '@/stores/reservation.store'
import SgiltFormGroup from '@/components/basics/inputs/SgiltFormGroup.vue'
import NotAvailable from '@/components/reservation_form/NotAvailable.vue'
import BookingFlow from '@/components/booking_flow/BookingFlow.vue'

const partnerStore = usePartnerStore()
const partner = computed(() => partnerStore.partner)

const reservationStore = useReservationStore()

// -- date --
const chosenDateState = computed(() => {
  // is the selected date available, booked or option ?
  const date = reservationStore.dateReservation
  if (!date) return 'empty'
  if (bookedDates.value && dateArrayContains(bookedDates.value, date)) return 'booked'
  if (optionDates.value && dateArrayContains(optionDates.value, date)) return 'option'
  return 'available'
})

const dateIsAvailable = computed(() => !['option', 'booked'].includes(chosenDateState.value))

// -- booking --
const bookedDates = computed(() =>
  partner.value?.calendar?.filter((entry) => entry.state === 'booked').map((entry) => entry.date),
)

const optionDates = computed(() =>
  partner.value?.calendar?.filter((entry) => entry.state === 'option').map((entry) => entry.date),
)

// submit button
const showBookingFlow = ref<boolean>(false)
const handleBooking = () => {
  showBookingFlow.value = reservationStore.checkPriceValidity()
}
</script>

<style scoped lang="scss">
.reservation-form {
  width: 22rem;
  background: $color-white;
  padding: $spacing-l;
  border-radius: $border-radius-s;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  margin: $spacing-m auto;

  h2 {
    color: $color-primary;
    margin: 0 0 $spacing-m 0;
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
    text-align: center;
    h2 {
      font-size: $font-size-h1;
    }
  }
}
@include respond-to(mobile) {
  .reservation-form {
    width: initial;
  }
  .reservation-header {
    h2 {
      font-size: $font-size-h2;
      line-height: $line-height-h2;
    }
  }
}
</style>
