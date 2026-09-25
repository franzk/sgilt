import {
  MARIAGE_RUBRIQUES,
  RUBRIQUE_KEYS,
  RUBRIQUE_RESERVATION_STATUSES,
  type EventRubrique,
  type RubriqueKey,
  type RubriqueReservation,
  type RubriqueReservationStatus,
} from '~/constants/event-rubriques'
import { toISODate } from '~/utils/dateUtils'
import { EVENT_TYPE_DEFAULT_TITLES } from '~/utils/eventTypes'
import {
  AMBIANCE_OPTIONS,
  EVENT_TYPE_OPTIONS,
  MOMENT_CLE_OPTIONS,
  type DemandeOption,
} from '~/types/demande'

// Événement du parcours public (route /) : source de vérité unique tant qu'il n'est pas
// matérialisé en base, comme un panier d'e-boutique. Aucun appel réseau.
//
// Les noms de champs sont ceux de EventDetail (domaine de l'événement en base) : les écrans
// qui manipulent cet état ne dépendent que de ce composable et de ces noms, pour pouvoir
// être branchés sur la base (route /app) sans être réécrits.
//
// Ne contient aucune donnée de contact (prénom, nom, email, téléphone) : elles restent
// dans useDemande, qui est le brouillon d'une demande à un prestataire.
const LOCAL_EVENT_STORAGE_KEY = 'sgilt:evenement'
const LOCAL_EVENT_VERSION = 2
// Durée glissante : renouvelée à chaque modification. Passé ce délai, l'événement est effacé.
const LOCAL_EVENT_TTL_MS = 30 * 24 * 60 * 60 * 1000

export interface LocalEvent {
  eventType: string | null
  eventTypeAutre: string
  title: string
  date: Date | undefined
  ville: string
  lieu: string
  nbInvites: string
  ambiance: string | null
  ambianceAutre: string
  momentCle: string | null
  momentCleAutre: string
  description: string
  rubriques: EventRubrique[]
  // Mode événement : vrai une fois que l'utilisateur a choisi de commencer son organisation avec
  // Sgilt (/commencer). Détermine l'affichage du bandeau sur les pages publiques.
  organisationStarted: boolean
}

// Informations de l'événement modifiables par l'utilisateur (paramètres, récap de demande).
export type EventInfoFields = Pick<
  LocalEvent,
  | 'title'
  | 'eventType'
  | 'eventTypeAutre'
  | 'date'
  | 'ville'
  | 'lieu'
  | 'nbInvites'
  | 'ambiance'
  | 'ambianceAutre'
  | 'momentCle'
  | 'momentCleAutre'
  | 'description'
>

function defaultLocalEvent(): LocalEvent {
  return {
    eventType: null,
    eventTypeAutre: '',
    title: '',
    date: undefined,
    ville: '',
    lieu: '',
    nbInvites: '',
    ambiance: null,
    ambianceAutre: '',
    momentCle: null,
    momentCleAutre: '',
    description: '',
    rubriques: [],
    organisationStarted: false,
  }
}

// ── Sérialisation (Date ↔ 'YYYY-MM-DD') ───────────────────────────────────────

function serialize(event: LocalEvent) {
  return { ...event, date: event.date ? toISODate(event.date) : null }
}

function parseISODate(value: unknown): Date | undefined {
  if (typeof value !== 'string') return undefined
  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(value)
  if (!match) return undefined
  return new Date(Number(match[1]), Number(match[2]) - 1, Number(match[3]))
}

function isReservation(value: unknown): value is RubriqueReservation {
  if (typeof value !== 'object' || value === null) return false
  const { prestataireSlug, prestataireName, prestataireImage, status, sentAt } = value as Record<
    string,
    unknown
  >
  return (
    typeof prestataireSlug === 'string' &&
    typeof prestataireName === 'string' &&
    typeof prestataireImage === 'string' &&
    RUBRIQUE_RESERVATION_STATUSES.includes(status as RubriqueReservationStatus) &&
    parseISODate(sentAt) !== undefined
  )
}

function deserializeRubrique(value: unknown): EventRubrique | null {
  if (typeof value !== 'object' || value === null) return null
  const { key, reservations } = value as Record<string, unknown>
  if (!RUBRIQUE_KEYS.includes(key as RubriqueKey)) return null
  return {
    key: key as RubriqueKey, // garanti par le includes ci-dessus
    reservations: Array.isArray(reservations) ? reservations.filter(isReservation) : [],
  }
}

const asString = (value: unknown): string => (typeof value === 'string' ? value : '')
const asStringOrNull = (value: unknown): string | null => (typeof value === 'string' ? value : null)
const asBoolean = (value: unknown): boolean => value === true

function deserialize(raw: Record<string, unknown>): LocalEvent {
  return {
    eventType: asStringOrNull(raw.eventType),
    eventTypeAutre: asString(raw.eventTypeAutre),
    title: asString(raw.title),
    date: parseISODate(raw.date),
    ville: asString(raw.ville),
    lieu: asString(raw.lieu),
    nbInvites: asString(raw.nbInvites),
    ambiance: asStringOrNull(raw.ambiance),
    ambianceAutre: asString(raw.ambianceAutre),
    momentCle: asStringOrNull(raw.momentCle),
    momentCleAutre: asString(raw.momentCleAutre),
    description: asString(raw.description),
    rubriques: Array.isArray(raw.rubriques)
      ? raw.rubriques
          .map(deserializeRubrique)
          .filter((rubrique): rubrique is EventRubrique => rubrique !== null)
      : [],
    organisationStarted: asBoolean(raw.organisationStarted),
  }
}

// ── Storage ───────────────────────────────────────────────────────────────────

function readStorage(): LocalEvent | null {
  if (!import.meta.client) return null
  try {
    const raw = localStorage.getItem(LOCAL_EVENT_STORAGE_KEY)
    if (!raw) return null
    const parsed = JSON.parse(raw)
    const expired =
      typeof parsed.updatedAt !== 'number' || Date.now() - parsed.updatedAt > LOCAL_EVENT_TTL_MS
    if (parsed.v !== LOCAL_EVENT_VERSION || expired) {
      localStorage.removeItem(LOCAL_EVENT_STORAGE_KEY)
      return null
    }
    return deserialize(parsed.event ?? {})
  } catch {
    return null
  }
}

// Un événement identique à l'état par défaut n'est pas stocké : la clé disparaît.
// C'est ce qui rend reset() atomique sans avoir à couper le watcher.
function writeStorage(event: LocalEvent) {
  if (!import.meta.client) return
  try {
    const serialized = serialize(event)
    if (JSON.stringify(serialized) === JSON.stringify(serialize(defaultLocalEvent()))) {
      localStorage.removeItem(LOCAL_EVENT_STORAGE_KEY)
      return
    }
    localStorage.setItem(
      LOCAL_EVENT_STORAGE_KEY,
      JSON.stringify({ v: LOCAL_EVENT_VERSION, updatedAt: Date.now(), event: serialized }),
    )
  } catch {
    // Stockage indisponible (navigation privée, quota) : l'état reste en mémoire.
  }
}

// ── Singleton state ───────────────────────────────────────────────────────────
// Même pattern que useDemande : reactive au niveau module, initialisé depuis le
// storage une seule fois, puis persisté à chaque modification.

const localEvent = reactive<LocalEvent>({ ...defaultLocalEvent(), ...readStorage() })

if (import.meta.client) {
  watch(localEvent, () => writeStorage(toRaw(localEvent)), { deep: true })
}

// ── Libellés des choix (type, ambiance, moment clé) ───────────────────────────

export function choiceLabel(
  options: DemandeOption[],
  value: string | null,
  autre: string,
): string | null {
  if (!value) return null
  if (value === 'autre') return autre || 'Autre'
  return options.find((option) => option.value === value)?.label ?? null
}

function choiceEmoji(options: DemandeOption[], value: string | null): string {
  if (!value) return ''
  return options.find((option) => option.value === value)?.emoji ?? '•••'
}

// ── Composable ────────────────────────────────────────────────────────────────

export function useLocalEvent() {
  // Efface l'événement local (bouton « effacer », déconnexion, abandon, matérialisation).
  function reset() {
    Object.assign(localEvent, defaultLocalEvent())
  }

  // Choix du type d'événement. Un seul événement local à la fois : un type différent
  // repart de zéro, le même type conserve ce qui a déjà été saisi.
  function start(eventType: string) {
    if (localEvent.eventType === eventType) return
    reset()
    localEvent.eventType = eventType
    localEvent.title = EVENT_TYPE_DEFAULT_TITLES[eventType] ?? EVENT_TYPE_DEFAULT_TITLES.autre!
  }

  // Seul le preset Mariage existe pour l'instant. N'injecte que ce qui manque :
  // un événement déjà initialisé (rubriques présentes) n'est jamais écrasé. Le titre par
  // défaut est déjà posé par start() — ce repli ne joue que pour un accès direct à /evenement
  // sans être passé par /fete.
  function initMariage() {
    if (!localEvent.title) localEvent.title = EVENT_TYPE_DEFAULT_TITLES.mariage!
    if (localEvent.rubriques.length === 0) {
      localEvent.rubriques = MARIAGE_RUBRIQUES.map((rubrique) => ({
        ...rubrique,
        reservations: [],
      }))
    }
  }

  const eventTypeLabel = computed(() =>
    choiceLabel(EVENT_TYPE_OPTIONS, localEvent.eventType, localEvent.eventTypeAutre),
  )
  const eventTypeEmoji = computed(() => choiceEmoji(EVENT_TYPE_OPTIONS, localEvent.eventType))
  const ambianceLabel = computed(() =>
    choiceLabel(AMBIANCE_OPTIONS, localEvent.ambiance, localEvent.ambianceAutre),
  )
  const ambianceEmoji = computed(() => choiceEmoji(AMBIANCE_OPTIONS, localEvent.ambiance))
  const momentCleLabel = computed(() =>
    choiceLabel(MOMENT_CLE_OPTIONS, localEvent.momentCle, localEvent.momentCleAutre),
  )
  const momentCleEmoji = computed(() => choiceEmoji(MOMENT_CLE_OPTIONS, localEvent.momentCle))

  return {
    localEvent,
    start,
    reset,
    initMariage,
    eventTypeLabel,
    eventTypeEmoji,
    ambianceLabel,
    ambianceEmoji,
    momentCleLabel,
    momentCleEmoji,
  }
}
