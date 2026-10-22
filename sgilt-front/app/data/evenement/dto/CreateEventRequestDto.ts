/**
 * DTO — contrat de requête de POST /user/events : les données de l'événement et, s'il naît d'une
 * demande à un prestataire, cette première demande. Les rubriques sont déduites par le back.
 */
import type { EvenementRequest } from '~/types/evenement'

export interface CreateEventRequestDto extends EvenementRequest {
  demande: { prestataireId: string; message: string | null } | null
}
