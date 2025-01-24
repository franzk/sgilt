import type { Partner, Price } from '@/data/domain/Partner'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useReservationStore = defineStore('reservation', () => {
  // partner booked
  const partner = ref<Partner>()

  // booking date
  const dateReservation = ref<Date>()

  // booking chosen price and error
  const price = ref<Price | undefined>()
  const priceError = ref<string | undefined>()

  return { partner, dateReservation, price, priceError }
})
