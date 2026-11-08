/**
 * Couche service — orchestration des appels API des commandes de billets
 */
import { FetchError } from 'ofetch'
import { createCommandeApi, eBilletPdfUrlApi, getCommandeApi, getEBilletApi } from '../api/commandeApi'
import { mapCommande, mapCommandeSummary, mapEBillet } from '../mapper/commandeMapper'
import type { Commande, CommandeFailure, CommandeSummary, EBillet } from '../domain/Commande'
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
 * Relit une commande, avec son statut : PAID dès que le back a reçu la confirmation du paiement.
 */
export async function getCommande(id: string): Promise<CommandeSummary> {
  return mapCommandeSummary(await getCommandeApi(id))
}

/**
 * Relit l'e-billet d'une commande payée : l'événement, ses infos pratiques, le nombre de places et
 * le QR, déjà dessiné par le back.
 */
export async function getEBillet(id: string): Promise<EBillet> {
  return mapEBillet(await getEBilletApi(id))
}

/**
 * URL de téléchargement du PDF de l'e-billet d'une commande payée (nommé e-billet-<slug>.pdf par
 * le back).
 */
export function getEBilletPdfUrl(id: string): string {
  return eBilletPdfUrlApi(id)
}

/**
 * Commande inconnue du back (404) : l'URL de confirmation ne désigne aucune commande. Pour
 * l'e-billet, aussi une commande pas (encore) payée.
 */
export function isCommandeNotFound(error: unknown): boolean {
  return error instanceof FetchError && error.statusCode === 404
}

/**
 * Motif d'échec de la création d'une commande. Le back répond sans corps : 409 = plus assez de
 * places, 400 = billetterie pas ouverte, 404 = événement ou billetterie introuvable, 429 = trop de
 * commandes depuis cette IP (gateway) ; toute autre erreur est TECHNICAL.
 */
export function toCommandeFailure(error: unknown): CommandeFailure {
  if (!(error instanceof FetchError)) return 'TECHNICAL'
  if (error.statusCode === 409) return 'NOT_ENOUGH_TICKETS'
  if (error.statusCode === 400 || error.statusCode === 404) return 'UNAVAILABLE'
  if (error.statusCode === 429) return 'TOO_MANY_ATTEMPTS'
  return 'TECHNICAL'
}
