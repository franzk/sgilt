/**
 * Middleware des URL sous /e/[slug]/ (parcours d'achat de billets) : tout ce qui n'a pas lieu
 * d'être renvoie vers la fiche de l'événement, qui en affiche l'état. Seul endroit où ces règles
 * vivent ; il passe avant la page, qui peut donc compter sur une billetterie.
 *  - événement inconnu ou sans billetterie ;
 *  - commande, paiement : billetterie pas encore ouverte, fermée ou complète ;
 *  - commande : pas de commande en cours pour cet événement (« Acheter mes billets » pas cliqué) ;
 *  - paiement : commande pas validée sur l'écran de commande ;
 *  - confirmation : pas de commande dans l'URL (?commande=), quel que soit l'état de la
 *    billetterie — l'acheteur de la dernière place voit sa confirmation d'un événement devenu
 *    complet. Le statut vient du back (page), pas du sessionStorage : l'URL suffit, même après
 *    la redirection 3D Secure ou dans un autre onglet ;
 *  - e-billet : pas de commande dans l'URL, même règle que la confirmation (commande inconnue ou
 *    pas payée : c'est la page qui renvoie à la fiche, sur le 404 du back) ;
 *  - toute autre URL (inexistante, attrapée par /e/[slug]/[...path]).
 */
import { loadPublicEvent } from '~/composables/usePublicEvent'
import { useTicketOrder } from '~/composables/useTicketOrder'
import { canPurchase } from '~/utils/ticketingUtils'

export default defineNuxtRouteMiddleware(async (to) => {
  const slug = String(to.params.slug)
  const toEvent = () => navigateTo(`/e/${slug}`, { replace: true })

  // Slug inconnu : la fiche répond 404.
  const ticketing = (await loadPublicEvent(slug))?.ticketing
  if (!ticketing) return toEvent()

  const { stage } = useTicketOrder(slug, ticketing).order.value

  switch (to.name) {
    case 'e-slug-commande':
      if (!canPurchase(ticketing) || (stage !== 'STARTED' && stage !== 'ORDERED')) return toEvent()
      return
    case 'e-slug-paiement':
      if (!canPurchase(ticketing) || stage !== 'ORDERED') return toEvent()
      return
    case 'e-slug-confirmation':
    case 'e-slug-e-billet':
      if (typeof to.query.commande !== 'string' || !to.query.commande) return toEvent()
      return
    default:
      return toEvent()
  }
})
