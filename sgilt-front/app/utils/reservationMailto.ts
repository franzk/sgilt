import type { ProReservationDetail } from '~/data/reservation/domain/ProReservationDetail'

/**
 * Construit une URL mailto: pré-remplie pour qu'un prestataire contacte le client d'une réservation.
 * La signature utilise toujours le nom professionnel du prestataire (nom de groupe, d'entreprise…).
 *
 * @param reservation les détails de la réservation (event + clientInfo + prestataireName)
 */
export function buildReservationMailto(reservation: ProReservationDetail): string {
  const { email, firstName } = reservation.clientInfo
  const subject = encodeURIComponent(`Votre demande — ${reservation.event.title}`)
  const body = encodeURIComponent(
    `Bonjour ${firstName},\n\nJe reviens vers vous suite à votre demande pour ${reservation.event.title}.\n\nCordialement,\n${reservation.prestataireName}`,
  )
  return `mailto:${email}?subject=${subject}&body=${body}`
}
