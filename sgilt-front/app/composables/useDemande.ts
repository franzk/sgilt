import {
  type DemandeRequest,
  type OnboardingDemandeRequest,
  type DemandeState,
  EVENT_TYPE_OPTIONS,
  AMBIANCE_OPTIONS,
  MOMENT_CLE_OPTIONS,
} from '~/types/demande'
import { submitOnboarding } from '~/data/onboarding/api/onboardingApi'
import { createEventApi } from '~/data/evenement/api/evenementApi'

const DEMANDE_STORAGE_KEY = 'sgilt:demande'
const storage = () => sessionStorage

// ── Serialization (handles Date ↔ ISO string) ─────────────────────────────────

function readStorage(): { state: DemandeState; etape: number } | null {
  if (!import.meta.client) return null
  try {
    const raw = storage().getItem(DEMANDE_STORAGE_KEY)
    if (!raw) return null
    const parsed = JSON.parse(raw)
    return {
      etape: typeof parsed.etape === 'number' ? parsed.etape : 1,
      state: {
        ...parsed.state,
        date: parsed.state?.date ? new Date(parsed.state.date) : undefined,
      },
    }
  } catch {
    return null
  }
}

function writeStorage(s: DemandeState, etape: number) {
  if (!import.meta.client) return
  storage().setItem(
    DEMANDE_STORAGE_KEY,
    JSON.stringify({ etape, state: { ...s, date: s.date?.toISOString() ?? null } }),
  )
}

function clearStorage() {
  if (!import.meta.client) return
  storage().removeItem(DEMANDE_STORAGE_KEY)
}

// ── Default state factory ─────────────────────────────────────────────────────

function defaultDemandeState(): DemandeState {
  return {
    prestataireId: null,
    prestataireName: '',
    prestataireImage: '',
    prestataireSlug: '',
    eventType: null,
    eventTypeAutre: '',
    ambiance: null,
    ambianceAutre: '',
    momentCle: null,
    momentCleAutre: '',
    description: '',
    date: undefined,
    ville: '',
    nbInvites: '',
    lieuDefini: true,
    lieu: '',
    prenom: '',
    nom: '',
    email: '',
    telephone: '',
    prestataireMessage: '',
  }
}

// ── Singleton state (module-level, shared across all useDemande calls) ─────────

const _stored = import.meta.client ? readStorage() : null

const etapeActuelle = ref<number>(_stored?.etape ?? 1)
const direction = ref<'forward' | 'back'>('forward')
const submitted = ref(false)
const submitting = ref(false)
const submitError = ref<string | null>(null)
const state = reactive<DemandeState>({
  ...defaultDemandeState(),
  ...(_stored?.state ?? {}),
})

// ── Persistance réactive ──────────────────────────────────────────────────────
// Le watcher écrit dans le sessionStorage à chaque modification de l'état ou de
// l'étape, pour que l'utilisateur retrouve sa progression en cas de refresh.
//
// On expose start/stop plutôt qu'un watcher nu parce que reset() doit muter
// l'état ET vider le storage de façon atomique. Sans ça, le watcher (asynchrone
// par défaut avec flush:'pre') se déclenche après clearStorage() et réécrit les
// valeurs par défaut — la clé réapparaît aussitôt supprimée. En coupant le
// watcher avant les mutations et en le relançant après, on garantit qu'aucune
// écriture parasite ne vient contredire le clearStorage().

// Référence vers la fonction d'arrêt retournée par watch() — null si le watcher
// n'est pas encore démarré ou a été stoppé (ex. pendant un reset()).
let _stopPersistWatcher: (() => void) | null = null

// Démarre le watcher et stocke son handle d'arrêt.
// Appelé au boot du module, puis après chaque reset().
function _startPersistWatcher() {
  _stopPersistWatcher = watch(
    [etapeActuelle, () => toRaw(state)],
    () => writeStorage(toRaw(state), etapeActuelle.value),
    { deep: true },
  )
}

if (import.meta.client) {
  _startPersistWatcher()
}

// ── Composable ────────────────────────────────────────────────────────────────

export function useDemande() {
  function next() {
    if (etapeActuelle.value < 6) {
      direction.value = 'forward'
      etapeActuelle.value++
    }
  }

  function back() {
    if (etapeActuelle.value > 1) {
      direction.value = 'back'
      etapeActuelle.value--
    }
  }

  function goTo(etape: number) {
    if (etape >= 1 && etape <= 6) {
      direction.value = etape > etapeActuelle.value ? 'forward' : 'back'
      etapeActuelle.value = etape
    }
  }

  function reset() {
    _stopPersistWatcher?.()
    Object.assign(state, defaultDemandeState())
    etapeActuelle.value = 1
    direction.value = 'forward'
    submitted.value = false
    useSearchUi().dateModel.value = undefined
    clearStorage()
    _startPersistWatcher()
  }

  function initDemande(
    id: string,
    name: string,
    image: string,
    slug: string,
    date: Date | undefined,
  ) {
    state.prestataireId = id
    state.prestataireName = name
    state.prestataireImage = image
    state.prestataireSlug = slug
    state.date = date
  }

  async function submit() {
    submitting.value = true
    submitError.value = null
    try {
      const resolvedEventType =
        state.eventType === 'autre' ? state.eventTypeAutre || null : state.eventType
      const resolvedAmbiance =
        state.ambiance === 'autre' ? state.ambianceAutre || null : state.ambiance
      const resolvedMomentCle =
        state.momentCle === 'autre' ? state.momentCleAutre || null : state.momentCle

      const d = state.date
      const dateStr = d
        ? `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
        : null

      if (!state.prestataireId) {
        submitError.value = 'Prestataire manquant'
        return
      }

      const eventBody: DemandeRequest = {
        prestataireId: state.prestataireId,
        eventType: resolvedEventType,
        ambiance: resolvedAmbiance,
        momentCle: resolvedMomentCle,
        description: state.description || null,
        date: dateStr,
        ville: state.ville || null,
        nbInvites: state.nbInvites || null,
        lieu: state.lieuDefini ? state.lieu || null : null,
        prestataireMessage: state.prestataireMessage || null,
      }

      if (useFlow().currentFlow.value === 'new-event') {
        const { eventId } = await createEventApi(eventBody)
        clearStorage()
        submitted.value = true
        useFlow().flowPayload.value = { id: eventId }
      } else {
        const onboardingBody: OnboardingDemandeRequest = {
          ...eventBody,
          firstName: state.prenom,
          lastName: state.nom,
          email: state.email,
          telephone: state.telephone || null,
        }
        await submitOnboarding(onboardingBody)
        clearStorage()
        submitted.value = true
      }
    } catch (err) {
      console.error('submit error:', err)
      submitError.value = 'Une erreur est survenue. Veuillez réessayer.'
    } finally {
      submitting.value = false
    }
  }

  // ── Labels ────────────────────────────────────────────────────────────────

  const eventTypeLabel = computed(() => {
    if (!state.eventType) return null
    if (state.eventType === 'autre') return state.eventTypeAutre || 'Autre'
    return EVENT_TYPE_OPTIONS.find((o) => o.value === state.eventType)?.label ?? null
  })

  const eventTypeEmoji = computed(() => {
    if (!state.eventType) return ''
    return EVENT_TYPE_OPTIONS.find((o) => o.value === state.eventType)?.emoji ?? '•••'
  })

  const ambianceLabel = computed(() => {
    if (!state.ambiance) return null
    if (state.ambiance === 'autre') return state.ambianceAutre || 'Autre'
    return AMBIANCE_OPTIONS.find((o) => o.value === state.ambiance)?.label ?? null
  })

  const ambianceEmoji = computed(() => {
    if (!state.ambiance) return ''
    return AMBIANCE_OPTIONS.find((o) => o.value === state.ambiance)?.emoji ?? '•••'
  })

  const momentCleLabel = computed(() => {
    if (!state.momentCle) return null
    if (state.momentCle === 'autre') return state.momentCleAutre || 'Autre'
    return MOMENT_CLE_OPTIONS.find((o) => o.value === state.momentCle)?.label ?? null
  })

  const momentCleEmoji = computed(() => {
    if (!state.momentCle) return ''
    return MOMENT_CLE_OPTIONS.find((o) => o.value === state.momentCle)?.emoji ?? '•••'
  })

  return {
    etapeActuelle,
    direction,
    submitted,
    submitting,
    submitError,
    state,
    initDemande,
    next,
    back,
    goTo,
    reset,
    submit,
    eventTypeLabel,
    eventTypeEmoji,
    ambianceLabel,
    ambianceEmoji,
    momentCleLabel,
    momentCleEmoji,
  }
}
