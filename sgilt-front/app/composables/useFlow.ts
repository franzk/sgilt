import { toISODate } from '~/utils/dateUtils'

export type Flow = 'new-event' | 'add-prestataire' | null

const FLOW_STORAGE_KEY = 'sgilt:flow'
const storage = () => sessionStorage

// ── Storage ───────────────────────────────────────────────────────────────────
// Persistance dans le sessionStorage pour survivre à un refresh de page.
// Les dates sont sérialisées en ISO string et reconstituées en Date à la lecture.

function readStorage(): { current: Flow; payload: any; label: string | null } | null {
  if (!import.meta.client) return null
  try {
    const raw = storage().getItem(FLOW_STORAGE_KEY)
    if (!raw) return null
    const parsed = JSON.parse(raw)
    return {
      current: parsed.current ?? null,
      label: parsed.label ?? null,
      payload: parsed.payload
        ? {
            ...parsed.payload,
            date: parsed.payload.date ? new Date(parsed.payload.date) : undefined,
          }
        : null,
    }
  } catch {
    return null
  }
}

function writeStorage(current: Flow, payload: any, label: string | null) {
  if (!import.meta.client) return
  storage().setItem(
    FLOW_STORAGE_KEY,
    JSON.stringify({
      current,
      label,
      payload: payload ? { ...payload, date: toISODate(payload.date) } : null,
    }),
  )
}

function clearStorage() {
  if (!import.meta.client) return
  storage().removeItem(FLOW_STORAGE_KEY)
}

// ── Singleton state ───────────────────────────────────────────────────────────
// Même pattern que useDemande : ref Vue au niveau module, pas useState Nuxt.
// Initialisé depuis le storage une seule fois au démarrage du module.

const _stored = import.meta.client ? readStorage() : null

const currentFlow = ref<Flow>(_stored?.current ?? null)
const flowPayload = ref<any>(_stored?.payload ?? null)
const flowLabel = ref<string | null>(_stored?.label ?? null)

// Persistance réactive : toute modification de l'état est écrite en storage.
if (import.meta.client) {
  watch(
    [currentFlow, flowPayload, flowLabel],
    () => writeStorage(currentFlow.value, flowPayload.value, flowLabel.value),
    { deep: true },
  )
}

// ── Composable ────────────────────────────────────────────────────────────────

export function useFlow() {
  // Définition des handlers par flow.
  // start()   — actions déclenchées à l'entrée du parcours (navigation, pré-remplissage…)
  // abort()   — annulation en cours de route (retour à l'origine)
  // success() — fin normale du parcours (null = pas d'écran de succès dédié)
  const flows: Record<
    NonNullable<Flow>,
    { start: () => void; abort: () => void; success: (() => void) | null }
  > = {
    'new-event': {
      start: () => {
        // Redirige vers la recherche pour choisir un prestataire
        navigateTo('/')
      },
      abort: () => {
        useSearchUi().dateModel.value = undefined
        navigateTo('/app/events')
      },
      success: () => {
        // nettoyage post-succès si besoin
      },
    },

    'add-prestataire': {
      start: () => {
        // Pré-remplit la date de recherche depuis le contexte de l'événement
        const date = flowPayload.value?.date
        useSearchUi().dateModel.value = date ? new Date(date) : undefined
        navigateTo('/search')
      },
      abort: () => {
        const eventId = flowPayload.value?.id
        useSearchUi().dateModel.value = undefined
        navigateTo(`/app/events/${eventId}`)
      },
      success: () => {
        // Retour à l'EventBoard une fois la demande envoyée
        const eventId = flowPayload.value?.id
        useSearchUi().dateModel.value = undefined
        navigateTo(`/app/events/${eventId}`)
      },
    },
  }

  // Démarre un flow : enregistre le contexte, affiche le bandeau, lance le handler.
  function start(flow: NonNullable<Flow>, label: string, payload?: any) {
    currentFlow.value = flow
    flowPayload.value = payload ?? null
    flowLabel.value = label
    flows[flow].start()
  }

  // Abandonne le flow en cours : exécute le handler d'annulation puis nettoie l'état.
  function abort() {
    flows[currentFlow.value!].abort()
    currentFlow.value = null
    flowPayload.value = null
    flowLabel.value = null
    clearStorage()
  }

  // Termine le flow avec succès : exécute le handler de fin puis nettoie l'état.
  function onFlowSuccess() {
    flows[currentFlow.value!].success?.()
    currentFlow.value = null
    flowPayload.value = null
    flowLabel.value = null
    clearStorage()
  }

  const showContextBanner = computed(
    () => currentFlow.value === 'add-prestataire' || currentFlow.value === 'new-event',
  )

  return { currentFlow, flowPayload, flowLabel, showContextBanner, start, abort, onFlowSuccess }
}
