import type { AppNotification } from '~/types/notification'

const PAGE_SIZE = 10

const ALL_NOTIFICATIONS: AppNotification[] = [
  {
    id: 'n1',
    type: 'state_change',
    read: false,
    createdAt: new Date(Date.now() - 1000 * 60 * 8).toISOString(),
    title: 'Demande confirmée',
    body: 'DJ Animation a confirmé votre demande pour le Mariage de Julie & Thomas.',
    href: '/app/events/evt-001/reservations/res-001',
  },
  {
    id: 'n2',
    type: 'new_note',
    read: false,
    createdAt: new Date(Date.now() - 1000 * 60 * 42).toISOString(),
    title: 'Nouvelle note',
    body: 'Léo Clairmont Photographe a ajouté une note sur votre demande.',
    href: '/app/events/evt-001/reservations/res-002',
  },
  {
    id: 'n3',
    type: 'new_document',
    read: false,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 3).toISOString(),
    title: 'Nouveau document',
    body: 'Éclat Gourmet a déposé un devis sur votre demande.',
    href: '/app/events/evt-001/reservations/res-003',
  },
  {
    id: 'n4',
    type: 'state_change',
    read: true,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 6).toISOString(),
    title: 'Demande en discussion',
    body: 'Gypsy Reed Ensemble a répondu à votre demande pour le Mariage de Julie & Thomas.',
    href: '/app/events/evt-001/reservations/res-004',
  },
  {
    id: 'n5',
    type: 'new_request',
    read: true,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(),
    title: 'Nouvelle demande',
    body: 'Sophie L. vous a envoyé une demande pour un anniversaire surprise à Lyon.',
    href: '/pro/reservations/pro-002',
  },
  {
    id: 'n6',
    type: 'state_change',
    read: true,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 26).toISOString(),
    title: 'Demande annulée',
    body: 'Votre demande auprès de Starlight Pulse a été annulée.',
    href: '/app/events/evt-001/reservations/res-006',
  },
  {
    id: 'n7',
    type: 'new_note',
    read: true,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 48).toISOString(),
    title: 'Nouvelle note',
    body: 'DJ Animation a ajouté une note sur votre demande.',
    href: '/app/events/evt-001/reservations/res-001',
  },
  {
    id: 'n8',
    type: 'new_request',
    read: true,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 50).toISOString(),
    title: 'Nouvelle demande',
    body: "Marc D. vous a envoyé une demande pour une soirée d'entreprise à Strasbourg.",
    href: '/pro/reservations/pro-003',
  },
  {
    id: 'n9',
    type: 'new_document',
    read: true,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 72).toISOString(),
    title: 'Nouveau document',
    body: 'DJ Animation a déposé un contrat sur votre demande.',
    href: '/app/events/evt-001/reservations/res-001',
  },
  {
    id: 'n10',
    type: 'state_change',
    read: true,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 96).toISOString(),
    title: 'Demande confirmée',
    body: 'Éclat Gourmet a confirmé votre demande pour le Mariage de Julie & Thomas.',
    href: '/app/events/evt-001/reservations/res-003',
  },
  {
    id: 'n11',
    type: 'new_note',
    read: true,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 120).toISOString(),
    title: 'Nouvelle note',
    body: 'Léo Clairmont Photographe a ajouté une note sur votre demande.',
    href: '/app/events/evt-001/reservations/res-002',
  },
  {
    id: 'n12',
    type: 'state_change',
    read: true,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 144).toISOString(),
    title: 'Demande en discussion',
    body: "Éclat Gourmet a répondu à votre demande pour l'Anniversaire 50 ans de Marc.",
    href: '/app/events/evt-002/reservations/res-011',
  },
]

export const NotificationMockService = {
  async fetchPage(page: number): Promise<AppNotification[]> {
    await new Promise((r) => setTimeout(r, 250))
    const start = page * PAGE_SIZE
    return ALL_NOTIFICATIONS.slice(start, start + PAGE_SIZE)
  },

  async markAsRead(id: string): Promise<void> {
    await new Promise((r) => setTimeout(r, 100))
    const n = ALL_NOTIFICATIONS.find((n) => n.id === id)
    if (n) n.read = true
  },

  async markAllAsRead(): Promise<void> {
    await new Promise((r) => setTimeout(r, 150))
    ALL_NOTIFICATIONS.forEach((n) => (n.read = true))
  },
}
