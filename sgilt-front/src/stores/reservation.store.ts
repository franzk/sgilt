import type { SgiltSelectOption } from '@/components/basics/inputs/SgiltSelect.vue'
import type { Partner, Price } from '@/data/domain/Partner'
import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'

export const useReservationStore = defineStore('reservation', () => {
  const { t } = useI18n()

  // booking date
  const dateReservation = ref<Date>()
  const dateError = ref<string | undefined>()
  watch(dateReservation, () => (dateError.value = undefined))

  // event time
  const timeReservation = ref<string>()
  const timeError = ref<string | undefined>()
  watch(timeReservation, () => (timeError.value = undefined))

  // event type
  const eventType = ref<SgiltSelectOption>()

  // location
  const location = ref<string>()
  const locationError = ref<string | undefined>()
  watch(location, () => (locationError.value = undefined))

  // partner booked
  const partner = ref<Partner>()

  // chosen price
  const price = ref<Price | undefined>()
  const priceError = ref<string | undefined>()
  watch(price, () => {
    priceError.value = undefined
    quantityError.value = undefined
  })

  // quantity of the chosen price
  const quantity = ref<number>()
  const quantityError = ref<string | undefined>()
  watch(quantity, () => (quantityError.value = undefined))

  // client email
  const email = ref<string>()
  const emailError = ref<string | undefined>()
  watch(email, () => (emailError.value = undefined))

  /**
   * check if the form is valid
   * @returns {boolean} true if the form is valid
   */
  const checkPriceValidity = (): boolean => {
    let isValid = true
    if (!dateReservation.value) {
      dateError.value = t('reservation.form.date-error') // date is required
      isValid = false
    }
    if ((price.value?.price || '-1') === '-1') {
      priceError.value = t('reservation.form.price-error') // price is required
      isValid = false
    }
    if (
      price.value?.unity &&
      (!quantity.value || (price.value?.minQuantity && quantity.value < price.value.minQuantity))
    ) {
      // quantity is required
      quantityError.value = t('reservation.form.quantity-error', {
        count: price.value?.minQuantity,
        unity: price.value?.unity,
      })
      isValid = false
    }
    return isValid
  }

  const checkStepOne = (): boolean => {
    let isValid = true
    if (!location.value) {
      locationError.value = t('booking-flow.step-1.location.error') // location is required
      isValid = false
    }
    if (!timeReservation.value) {
      timeError.value = t('booking-flow.step-1.time.error') // time is required
      isValid = false
    }
    return isValid
  }

  const checkSecondValidation = (): boolean => {
    let isValid = true
    if (!email.value) {
      emailError.value = t('booking-flow.email-required-error') // email is required
      isValid = false
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/i.test(email.value.trim())) {
      emailError.value = t('booking-flow.email-invalid-error') // email is not valid
      isValid = false
    }
    return isValid
  }

  return {
    dateReservation,
    dateError,
    timeReservation,
    timeError,
    location,
    locationError,
    eventType,
    partner,
    price,
    priceError,
    quantity,
    quantityError,
    email,
    emailError,
    checkPriceValidity,
    checkStepOne,
    checkSecondValidation,
  }
})
