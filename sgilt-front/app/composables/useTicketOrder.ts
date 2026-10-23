/**
 * Composable — commande de billets en cours pour un événement, partagée par les écrans du parcours
 * d'achat (/e/[slug], commande, paiement, confirmation) : quantité et coordonnées de l'acheteur.
 * Gardée en mémoire (useState), une par événement : elle survit à la navigation dans le parcours
 * (retour arrière compris), pas à un rechargement de la page. Provisoire, en attendant une commande
 * persistée côté back. La quantité est plafonnée par le nombre de places restantes (domaine), pas
 * par une limite UI arbitraire.
 */
import { formatPrice } from '~/utils/priceUtils'

const MIN_QUANTITY = 1

export interface TicketBuyer {
  firstName: string
  lastName: string
  email: string
}

interface TicketOrder {
  quantity: number
  buyer: TicketBuyer
}

export function useTicketOrder(slug: string, unitPrice: number, remainingTickets: number) {
  const order = useState<TicketOrder>(`ticket-order-${slug}`, () => ({
    quantity: MIN_QUANTITY,
    buyer: { firstName: '', lastName: '', email: '' },
  }))

  const quantity = computed(() => order.value.quantity)
  const buyer = order.value.buyer

  function decrement(): void {
    order.value.quantity = Math.max(MIN_QUANTITY, order.value.quantity - 1)
  }

  function increment(): void {
    order.value.quantity = Math.min(remainingTickets, order.value.quantity + 1)
  }

  const unitPriceLabel = formatPrice(unitPrice)
  const totalLabel = computed(() => formatPrice(unitPrice * order.value.quantity))

  return { quantity, buyer, decrement, increment, unitPriceLabel, totalLabel }
}
