/**
 * Couche service — orchestration des appels API des commandes de billets
 */
import { FetchError } from 'ofetch'
import { createCommandeApi } from '../api/commandeApi'
import { mapCommande } from '../mapper/commandeMapper'
import type { Commande, CommandeFailure } from '../domain/Commande'
import type { TicketBuyer } from '~/composables/useTicketOrder'

/**
 * Crée la commande côté back : ses places sont bloquées le temps du paiement. Le prix n'est pas
 * envoyé, c'est celui de la billetterie.
 */
export async function createCommande(slug: string, quantity: number, buyer: TicketBuyer): Promise<Commande> {
  return mapCommande(
    await createCommandeApi({
      slug,
      quantity,
      firstName: buyer.firstName.trim(),
      lastName: buyer.lastName.trim(),
      email: buyer.email.trim(),
    }),
  )
}

/**
 * Motif d'échec de la création d'une commande. Le back répond sans corps : 409 = plus assez de
 * places, 400 = billetterie pas ouverte, 404 = événement ou billetterie introuvable ; toute autre
 * erreur est TECHNICAL.
 */
export function toCommandeFailure(error: unknown): CommandeFailure {
  if (!(error instanceof FetchError)) return 'TECHNICAL'
  if (error.statusCode === 409) return 'NOT_ENOUGH_TICKETS'
  if (error.statusCode === 400 || error.statusCode === 404) return 'UNAVAILABLE'
  return 'TECHNICAL'
}
