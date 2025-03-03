import type { EventActivity } from '@/data/domain/EventActivity'

/**
 * Get all event activities
 * @returns {EventActivity[]} eventActivities
 */
export const findAllEventActivities = (): EventActivity[] => {
  return eventActivities
}

const eventActivities: EventActivity[] = [
  {
    id: '1',
    date: new Date('2025-02-24T10:15:00'),
    partnerName: 'Food Truck Burgers',
    partnerAvatarUrl: './images/food-truck-burgers.jpg',
    eventActivityTypeId: 'reservation-sent',
    payload: {
      message: 'Votre demande a bien été envoyée au partenaire.',
    },
  },
  {
    id: '2',
    date: new Date('2025-02-24T11:00:00'),
    partnerName: 'DJ Animation',
    partnerAvatarUrl: './images/dj-animation.jpg',
    eventActivityTypeId: 'reservation-viewed',
    payload: {
      message: 'Le partenaire a vu votre demande de réservation.',
    },
  },
  {
    id: '3',
    date: new Date('2025-02-24T12:30:00'),
    partnerName: 'Gypsy Reed Ensemble',
    partnerAvatarUrl: './images/jazz-band.jpg',
    eventActivityTypeId: 'message-received',
    payload: {
      message: "Bonjour, pouvez-vous préciser le lieu de l'événement ?",
    },
  },
  {
    id: '4',
    date: new Date('2025-02-24T14:45:00'),
    partnerName: 'Food Truck Burgers',
    partnerAvatarUrl: './images/food-truck-burgers.jpg',
    eventActivityTypeId: 'reservation-approved',
    payload: {
      message: 'Le partenaire a accepté votre demande.',
    },
  },
  {
    id: '5',
    date: new Date('2025-02-24T16:10:00'),
    partnerName: 'DJ Animation',
    partnerAvatarUrl: './images/dj-animation.jpg',
    eventActivityTypeId: 'payment-required',
    payload: {
      amount: '150€',
      message: 'Le paiement est requis pour confirmer la réservation.',
    },
  },
  {
    id: '6',
    date: new Date('2025-02-24T17:30:00'),
    partnerName: 'Gypsy Reed Ensemble',
    partnerAvatarUrl: './images/jazz-band.jpg',
    eventActivityTypeId: 'payment-confirmed',
    payload: {
      amount: '200€',
      message: 'Votre paiement a été reçu. Réservation confirmée !',
    },
  },
  {
    id: '7',
    date: new Date('2025-02-24T18:00:00'),
    partnerName: 'DJ Animation',
    partnerAvatarUrl: './images/dj-animation.jpg',
    eventActivityTypeId: 'message-sent',
    payload: {
      message: 'Merci pour votre réponse ! On valide la prestation ?',
    },
  },
  {
    id: '8',
    date: new Date('2025-02-24T18:45:00'),
    partnerName: 'Food Truck Burgers',
    partnerAvatarUrl: './images/food-truck-burgers.jpg',
    eventActivityTypeId: 'reservation-updated',
    payload: {
      message: 'Le partenaire a mis à jour sa prestation.',
    },
  },
  {
    id: '9',
    date: new Date('2025-02-24T19:20:00'),
    partnerName: 'DJ Animation',
    partnerAvatarUrl: './images/dj-animation.jpg',
    eventActivityTypeId: 'reservation-canceled',
    payload: {
      message: 'Votre réservation a été annulée.',
    },
  },
  {
    id: '10',
    date: new Date('2025-02-24T20:00:00'),
    partnerName: 'Food Truck Burgers',
    partnerAvatarUrl: './images/food-truck-burgers.jpg',
    eventActivityTypeId: 'partner-unavailable',
    payload: {
      message: 'Le partenaire n’est plus disponible à cette date.',
    },
  },
]
