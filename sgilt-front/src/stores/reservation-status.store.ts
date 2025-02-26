import type { ReservationStatus, ReservationStatusKey } from '@/types/ReservationStatusType'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useReservationStatusStore = defineStore('statusStore', () => {
  const statusMap = ref<Map<ReservationStatusKey, ReservationStatus>>(
    new Map([
      [
        'pending',
        {
          color: 'orange',
          icon: 'Mail',
          state: 'Demande envoyée',
          action: 'En attente de réponse du partenaire.',
          subtext: 'Votre demande a été envoyée, le partenaire doit répondre.',
        },
      ],
      [
        'viewed',
        {
          color: 'blue',
          icon: 'Eye',
          state: 'Demande vue',
          action: "En attente d'acceptation.",
          subtext: "Votre demande a été vue par le partenaire, il doit maintenant l'accepter.",
        },
      ],
      [
        'approved',
        {
          color: 'green',
          icon: 'CreditCard',
          state: 'Demande acceptée',
          action: 'En attente de paiement.',
          subtext: 'Votre demande a été acceptée, vous pouvez maintenant payer.',
        },
      ],
      [
        'paid',
        {
          color: 'green',
          icon: 'Rocket',
          state: 'Réservation confirmée',
          action: 'Votre réservation est confirmée.',
          subtext:
            'Votre réservation est confirmée, vous pouvez maintenant profiter de votre événement.',
        },
      ],
      [
        'cancelled',
        {
          color: 'red',
          icon: 'Cross',
          state: 'Demande annulée',
          action: 'Votre demande a été annulée.',
          subtext: 'Votre demande a été annulée, vous pouvez en faire une nouvelle.',
        },
      ],
    ]),
  )

  const getStatus = (key?: StatusKey) => (key ? statusMap.value.get(key) : undefined)

  return { getStatus }
})
