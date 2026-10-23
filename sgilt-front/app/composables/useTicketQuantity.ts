/**
 * Composable — quantité de billets d'une commande et total associé, partagés par les écrans du
 * parcours d'achat (/e/[slug], commande, paiement, confirmation). La quantité est reprise depuis la
 * query `qty` pour ne pas la faire ressaisir d'un écran à l'autre (pas de panier persisté côté back
 * pour l'instant), et plafonnée par le nombre de places restantes (domaine), pas par une limite UI
 * arbitraire.
 */
import { formatPrice } from '~/utils/priceUtils'

const MIN_QUANTITY = 1

export function useTicketQuantity(unitPrice: number, remainingTickets: number) {
  const route = useRoute()

  const initialQty = Number(route.query.qty)
  const quantity = ref(
    Number.isInteger(initialQty) && initialQty >= MIN_QUANTITY && initialQty <= remainingTickets
      ? initialQty
      : MIN_QUANTITY,
  )

  function decrement(): void {
    quantity.value = Math.max(MIN_QUANTITY, quantity.value - 1)
  }

  function increment(): void {
    quantity.value = Math.min(remainingTickets, quantity.value + 1)
  }

  const unitPriceLabel = formatPrice(unitPrice)
  const totalLabel = computed(() => formatPrice(unitPrice * quantity.value))

  return { quantity, decrement, increment, unitPriceLabel, totalLabel }
}
