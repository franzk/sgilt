/**
 * Composable — commande de billets d'un événement, sur tout le parcours d'achat : fiche (/e/[slug],
 * « Acheter mes billets »), écrans commande, paiement, confirmation, et middleware `ticketing`.
 * Une seule commande en mémoire à la fois, comme un panier : en démarrer une écrase la précédente,
 * quel que soit son événement. Gardée dans le sessionStorage : elle survit au rechargement, pas à
 * la fermeture de l'onglet.
 *
 * Brouillon de la commande côté front (événement, quantité, acheteur). Une fois le back branché,
 * la commande y sera créée à la validation de l'écran de commande (blocage des places, paiement
 * Stripe), et le paiement comme la confirmation reposeront sur elle.
 *
 * Renvoie toujours une commande pour l'événement demandé : si la mémoire contient celle d'un autre
 * événement, ou rien, c'est une commande vierge à l'étape NONE, non enregistrée.
 */
import type { TicketingMockTicketing } from '~/data/ticketing/mockEvent'
import { formatPrice } from '~/utils/priceUtils'

const TICKET_ORDER_STORAGE_KEY = 'sgilt:ticket-order'
const TICKET_ORDER_VERSION = 1
const MIN_QUANTITY = 1

// Étape atteinte : NONE (pas de commande), STARTED à « Acheter mes billets », ORDERED à la
// validation de l'écran de commande, PAID au paiement. Le middleware `ticketing` n'ouvre chaque
// écran qu'à la sienne.
export type TicketOrderStage = 'NONE' | 'STARTED' | 'ORDERED' | 'PAID'

const STORED_STAGES: TicketOrderStage[] = ['STARTED', 'ORDERED', 'PAID']

export interface TicketBuyer {
  firstName: string
  lastName: string
  email: string
}

export interface TicketOrder {
  // Événement de la commande.
  slug: string
  quantity: number
  buyer: TicketBuyer
  stage: TicketOrderStage
}

function newOrder(slug: string, stage: TicketOrderStage): TicketOrder {
  return {
    slug,
    quantity: MIN_QUANTITY,
    buyer: { firstName: '', lastName: '', email: '' },
    stage,
  }
}

// ── Sérialisation ─────────────────────────────────────────────────────────────

const asString = (value: unknown): string => (typeof value === 'string' ? value : '')

function asStoredStage(value: unknown): TicketOrderStage | null {
  return STORED_STAGES.find((stage) => stage === value) ?? null
}

function deserialize(raw: Record<string, unknown>): TicketOrder | null {
  const stage = asStoredStage(raw.stage)
  const { slug, quantity } = raw
  if (typeof slug !== 'string' || !slug || !stage) return null
  if (typeof quantity !== 'number' || !Number.isInteger(quantity) || quantity < MIN_QUANTITY) {
    return null
  }
  const isObject = typeof raw.buyer === 'object' && raw.buyer !== null
  const buyer = isObject ? (raw.buyer as Record<string, unknown>) : {}
  return {
    slug,
    quantity,
    buyer: {
      firstName: asString(buyer.firstName),
      lastName: asString(buyer.lastName),
      email: asString(buyer.email),
    },
    stage,
  }
}

// ── Storage ───────────────────────────────────────────────────────────────────

function readStorage(): TicketOrder | null {
  if (!import.meta.client) return null
  try {
    const raw = sessionStorage.getItem(TICKET_ORDER_STORAGE_KEY)
    if (!raw) return null
    const parsed = JSON.parse(raw)
    if (parsed.v !== TICKET_ORDER_VERSION) {
      sessionStorage.removeItem(TICKET_ORDER_STORAGE_KEY)
      return null
    }
    return deserialize(parsed.order ?? {})
  } catch {
    return null
  }
}

function writeStorage(order: TicketOrder | null) {
  if (!import.meta.client) return
  try {
    if (!order) {
      sessionStorage.removeItem(TICKET_ORDER_STORAGE_KEY)
      return
    }
    sessionStorage.setItem(
      TICKET_ORDER_STORAGE_KEY,
      JSON.stringify({ v: TICKET_ORDER_VERSION, order }),
    )
  } catch {
    // Stockage indisponible (navigation privée, quota) : la commande reste en mémoire.
  }
}

// ── Singleton state ───────────────────────────────────────────────────────────
// Même pattern que useLocalEvent : état au niveau module, initialisé depuis le storage une seule
// fois, puis persisté à chaque modification. Côté serveur, il reste vide (jamais modifié).

const storedOrder = ref<TicketOrder | null>(readStorage())

if (import.meta.client) {
  watch(storedOrder, (order) => writeStorage(order), { deep: true })
}

// ── Composable ────────────────────────────────────────────────────────────────

// ticketing : billetterie de l'événement (usePublicEvent), pour le prix et le plafond.
export function useTicketOrder(slug: string, ticketing: TicketingMockTicketing | undefined) {
  // Sans billetterie, aucun écran d'achat n'est atteint (middleware `ticketing`) : les valeurs
  // par défaut ne servent jamais.
  const unitPrice = ticketing?.unitPrice ?? 0
  const remainingTickets = ticketing?.remainingTickets ?? 0

  const order = computed<TicketOrder>(() =>
    storedOrder.value?.slug === slug ? storedOrder.value : newOrder(slug, 'NONE'),
  )

  // « Acheter mes billets » : reprend la commande en cours de l'événement (démarrée ou validée),
  // sinon en démarre une nouvelle qui écrase celle en mémoire.
  function buy(): void {
    if (order.value.stage === 'STARTED' || order.value.stage === 'ORDERED') return
    storedOrder.value = newOrder(slug, 'STARTED')
  }

  // ── Quantité (écran de commande) ──
  // Plafonnée par le nombre de places restantes (domaine), pas par une limite UI arbitraire ; ce
  // nombre n'est montré qu'à qui atteint le plafond. limitReached est propre à l'écran : il n'est
  // pas enregistré avec la commande.
  const limitReached = ref(false)

  function decrement(): void {
    limitReached.value = false
    order.value.quantity = Math.max(MIN_QUANTITY, order.value.quantity - 1)
  }

  // Au plafond, le « + » reste cliquable : le clic signale la limite au lieu de ne rien faire.
  function increment(): void {
    if (order.value.quantity >= remainingTickets) {
      limitReached.value = true
      return
    }
    order.value.quantity += 1
  }

  // ── Étapes ──
  function markOrdered(): void {
    order.value.stage = 'ORDERED'
  }

  function markPaid(): void {
    order.value.stage = 'PAID'
  }

  const unitPriceLabel = formatPrice(unitPrice)
  const totalLabel = computed(() => formatPrice(unitPrice * order.value.quantity))

  return {
    order,
    buy,
    remainingTickets,
    limitReached,
    decrement,
    increment,
    markOrdered,
    markPaid,
    unitPriceLabel,
    totalLabel,
  }
}
