/**
 * Domaine — métadonnées d'une réservation (sans le feed). Le slug du prestataire (pour charger sa
 * fiche publique) n'est fourni que côté client ; côté prestataire, c'est le sien.
 */
import type { ReservationSummary } from './ReservationSummary'

export interface ReservationMeta extends ReservationSummary {
  prestataireSlug?: string
}
