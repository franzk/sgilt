/**
 * DTOs — invitations client du BO (POST/GET /admin/invitations).
 */
import type { ClientInvitationStatus } from '../domain/ClientInvitationStatus'

export interface ClientInvitationDto {
  id: string
  firstName: string
  lastName: string
  email: string
  eventType: string
  eventDate: string
  prestataireName: string
  status: ClientInvitationStatus
  linkSentAt: string | null
  linkExpiresAt: string | null
}

export interface CreateClientInvitationRequestDto {
  firstName: string
  lastName: string
  email: string
  /** Facultatif : absent si non renseigné. */
  telephone: string | null
  eventType: string
  eventDate: string
  prestataireId: string
}
