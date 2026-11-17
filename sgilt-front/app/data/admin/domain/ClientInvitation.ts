/**
 * Domaine — invitation d'un client créé depuis le BO (deal conclu hors Sgilt) : le client, son
 * événement, la réservation confirmée chez un prestataire, et l'état de l'invitation.
 */

import type { ClientInvitationStatus } from './ClientInvitationStatus'

export interface ClientInvitation {
  id: string
  firstName: string
  lastName: string
  email: string
  eventType: string
  eventDate: string
  prestataireName: string
  status: ClientInvitationStatus
  /** Date d'envoi du dernier mail d'invitation, null tant qu'il n'est pas parti. */
  linkSentAt: string | null
  /** Expiration du lien d'activation, null tant que le mail n'est pas parti. */
  linkExpiresAt: string | null
}

/** Ce que l'admin saisit pour créer le client, son événement et sa réservation. */
export interface ClientInvitationCreation {
  firstName: string
  lastName: string
  email: string
  eventType: string
  eventDate: string
  prestataireId: string
}
