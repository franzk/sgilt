import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

export const useReservationStepsStore = defineStore('stepsStore', () => {
  const { t } = useI18n()

  const steps = ref([
    { value: 'pending', label: t('event.reservation.steps.pending') },
    { value: 'viewed', label: t('event.reservation.steps.viewed') },
    { value: 'approved', label: t('event.reservation.steps.approved') },
    { value: 'paid', label: t('event.reservation.steps.paid') },
  ])

  return { steps }
})
