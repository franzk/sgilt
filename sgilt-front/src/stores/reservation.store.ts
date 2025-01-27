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
  watch(dateReservation, () => {
    dateError.value = undefined
  })

  // event type
  const eventType = ref<SgiltSelectOption>()

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
  watch(quantity, () => {
    quantityError.value = undefined
  })

  /**
   * check if the form is valid
   * @returns {boolean} true if the form is valid
   */
  const checkValidity = (): boolean => {
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

  return {
    dateReservation,
    dateError,
    eventType,
    partner,
    price,
    priceError,
    quantity,
    quantityError,
    checkValidity,
  }
})
