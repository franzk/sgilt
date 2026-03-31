// useFlow — gestion centralisée des parcours utilisateur
//
// Un "flow" est un contexte de navigation guidée (ex : créer un événement,
// ajouter un prestataire). Il expose un bandeau contextuel, un payload
// de données, et des handlers start / abort / success.
//
// null = aucun parcours actif (visiteur ou navigation libre)

export type Flow = 'new-event' | 'add-prestataire' | null

// ── Storage ───────────────────────────────────────────────────────────────────
// Persistance dans le sessionStorage pour survivre à un refresh de page.
// Les dates sont sérialisées en ISO string et reconstituées en Date à la lecture.

const STORAGE_KEY = 'sgilt:flow'
const storage = () => sessionStorage

function readStorage(): { current: Flow; payload: any } | null {
  if (!import.meta.client) return null
  try {
    const raw = storage().getItem(STORAGE_KEY)
    if (!raw) return null
    const parsed = JSON.parse(raw)
    return {
      current: parsed.current ?? null,
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

function writeStorage(current: Flow, payload: any) {
  if (!import.meta.client) return
  storage().setItem(
    STORAGE_KEY,
    JSON.stringify({
      current,
      payload: payload
        ? { ...payload, date: payload.date instanceof Date ? payload.date.toISOString() : (payload.date ?? null) }
        : null,
    }),
  )
}

function clearStorage() {
  if (!import.meta.client) return
  storage().removeItem(STORAGE_KEY)
}

// ── Singleton state ───────────────────────────────────────────────────────────
// État partagé entre tous les appels à useFlow(), initialisé depuis le storage
// au démarrage du module (une seule fois pour la durée de vie de l'app).

const _stored = import.meta.client ? readStorage() : null

const currentFlow = useState<Flow>('flow:current', () => _stored?.current ?? null)
const flowPayload = useState<any>('flow:payload', () => _stored?.payload ?? null)

// Persistance réactive : toute modification de l'état est écrite en storage.
if (import.meta.client) {
  watch(
    [currentFlow, flowPayload],
    () => writeStorage(currentFlow.value, flowPayload.value),
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
        navigateTo('/app/reservations')
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
  function start(flow: NonNullable<Flow>, contextLabel: string, payload?: any) {
    currentFlow.value = flow
    flowPayload.value = payload ?? null
    useContextBanner().show(contextLabel, abort)
    flows[flow].start()
  }

  // Abandonne le flow en cours : exécute le handler d'annulation puis nettoie l'état.
  function abort() {
    flows[currentFlow.value!].abort()
    currentFlow.value = null
    flowPayload.value = null
    useContextBanner().hide()
    clearStorage()
  }

  // Termine le flow avec succès : exécute le handler de fin puis nettoie l'état.
  function onFlowSuccess() {
    flows[currentFlow.value!].success?.()
    currentFlow.value = null
    flowPayload.value = null
    useContextBanner().hide()
    clearStorage()
  }

  return { currentFlow, flowPayload, start, abort, onFlowSuccess }
}
