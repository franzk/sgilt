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
    eventActivityType: {
      id: 'reservation-sent',
      color: '#F7B731',
      icon: 'Mail',
    },
    payload: {
      message: 'Votre demande a bien été envoyée au partenaire.',
    },
  },
  {
    id: '2',
    date: new Date('2025-02-24T11:00:00'),
    partnerName: 'DJ Animation',
    partnerAvatarUrl: './images/dj-animation.jpg',
    eventActivityType: {
      id: 'reservation-viewed',
      color: '#45A0D9',
      icon: 'Eye',
    },
    payload: {
      message: 'Le partenaire a vu votre demande de réservation.',
    },
  },
  {
    id: '3',
    date: new Date('2025-02-24T12:30:00'),
    partnerName: 'Gypsy Reed Ensemble',
    partnerAvatarUrl: './images/jazz-band.jpg',
    eventActivityType: {
      id: 'message-received',
      color: '#8E44AD',
      icon: 'ChatBubble',
    },
    payload: {
      message: "Bonjour, pouvez-vous préciser le lieu de l'événement ?",
    },
  },
  {
    id: '4',
    date: new Date('2025-02-24T14:45:00'),
    partnerName: 'Food Truck Burgers',
    partnerAvatarUrl: './images/food-truck-burgers.jpg',
    eventActivityType: {
      id: 'reservation-approved',
      color: '#4CAF50',
      icon: 'Check',
    },
    payload: {
      message: 'Le partenaire a accepté votre demande.',
    },
  },
  {
    id: '5',
    date: new Date('2025-02-24T16:10:00'),
    partnerName: 'DJ Animation',
    partnerAvatarUrl: './images/dj-animation.jpg',
    eventActivityType: {
      id: 'payment-required',
      color: '#E67E22',
      icon: 'CreditCard',
    },
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
    eventActivityType: {
      id: 'payment-confirmed',
      color: '#27AE60',
      icon: 'Done',
    },
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
    eventActivityType: {
      id: 'message-sent',
      color: '#8E44AD',
      icon: 'Chat',
    },
    payload: {
      message: 'Merci pour votre réponse ! On valide la prestation ?',
    },
  },
  {
    id: '8',
    date: new Date('2025-02-24T18:45:00'),
    partnerName: 'Food Truck Burgers',
    partnerAvatarUrl: './images/food-truck-burgers.jpg',
    eventActivityType: {
      id: 'reservation-updated',
      color: '#3498DB',
      icon: 'EditNote',
    },
    payload: {
      message: 'Le partenaire a mis à jour sa prestation.',
    },
  },
  {
    id: '9',
    date: new Date('2025-02-24T19:20:00'),
    partnerName: 'DJ Animation',
    partnerAvatarUrl: './images/dj-animation.jpg',
    eventActivityType: {
      id: 'reservation-canceled',
      color: '#E74C3C',
      icon: 'Cancel',
    },
    payload: {
      message: 'Votre réservation a été annulée.',
    },
  },
  {
    id: '10',
    date: new Date('2025-02-24T20:00:00'),
    partnerName: 'Food Truck Burgers',
    partnerAvatarUrl: './images/food-truck-burgers.jpg',
    eventActivityType: {
      id: 'partner-unavailable',
      color: '#C0392B',
      icon: 'Block',
    },
    payload: {
      message: 'Le partenaire n’est plus disponible à cette date.',
    },
  },
]
