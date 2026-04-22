import {
  type DemandeRequest,
  type DemandeState,
  EVENT_TYPE_OPTIONS,
  AMBIANCE_OPTIONS,
  MOMENT_CLE_OPTIONS,
} from '~/types/demande'

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

// Persist on every change (client only, runs for the lifetime of the app)
if (import.meta.client) {
  watch(
    [etapeActuelle, () => toRaw(state)],
    () => writeStorage(toRaw(state), etapeActuelle.value),
    { deep: true },
  )
}

// ── Composable ────────────────────────────────────────────────────────────────

export function useDemande() {
  // dateModel (URL) est la source de vérité pour la date — state.date suit
  const { dateModel } = useSearchUi()
  watch(
    dateModel,
    (d) => {
      state.date = d
    },
    { immediate: true },
  )

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
    Object.assign(state, defaultDemandeState())
    etapeActuelle.value = 1
    direction.value = 'forward'
    submitted.value = false
    useSearchUi().dateModel.value = undefined
    clearStorage()
  }

  function setPrestataireId(id: string) {
    state.prestataireId = id
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

      const body: DemandeRequest = {
        firstName: state.prenom,
        lastName: state.nom,
        email: state.email,
        telephone: state.telephone || null,
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

      await apiFetch('/onboarding', { method: 'POST', body })
      // Clear storage so a refresh shows a fresh form, but keep in-memory
      // state so the confirmation screen can display the recap
      clearStorage()
      submitted.value = true
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
    setPrestataireId,
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
