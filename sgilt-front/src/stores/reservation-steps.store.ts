import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useReservationStepsStore = defineStore('stepsStore', () => {
  const steps = ref([
    { value: 'pending', label: 'Demande envoyée !' },
    { value: 'viewed', label: 'Votre partenaire a vu votre demande' },
    { value: 'approved', label: 'Il est prêt, à vous de payer !' },
    { value: 'paid', label: 'C’est réservé, préparez la fête !' },
  ])

  return { steps }
})
