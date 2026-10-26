/**
 * Règles d'état de la billetterie d'un événement, partagées par la fiche (/e/[slug]) et les écrans
 * du parcours d'achat.
 */
import type { TicketingMockTicketing } from '~/data/ticketing/mockEvent'

/**
 * « Complet » n'est pas un statut du back : c'est une billetterie ouverte sans place restante.
 */
export function isSoldOut(ticketing: TicketingMockTicketing): boolean {
  return ticketing.status === 'OPEN' && ticketing.remainingTickets === 0
}

/**
 * On ne peut acheter que si la billetterie est ouverte et qu'il reste des places.
 */
export function canPurchase(ticketing: TicketingMockTicketing): boolean {
  return ticketing.status === 'OPEN' && !isSoldOut(ticketing)
}
