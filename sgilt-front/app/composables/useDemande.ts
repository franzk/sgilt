import type { DemandeRequest, OnboardingDemandeRequest, DemandeState } from '~/types/demande'
import { submitOnboarding } from '~/data/onboarding/api/onboardingApi'
import { createEventApi } from '~/data/evenement/api/evenementApi'
import { toISODate } from '~/utils/dateUtils'

// Demande envoyée à un prestataire : le prestataire visé, les coordonnées de la personne et
// son message. L'événement n'est pas ici : il vit dans useLocalEvent (source de vérité),
// et n'est lu qu'au moment de l'envoi.
//
// État purement en mémoire, volontairement non persisté : les coordonnées ne sont jamais
// écrites dans un storage. Un rafraîchissement du tunnel les efface.

// ── Default state factory ─────────────────────────────────────────────────────

function defaultDemandeState(): DemandeState {
  return {
    prestataireId: null,
    prestataireName: '',
    prestataireImage: '',
    prestataireSlug: '',
    prenom: '',
    nom: '',
    email: '',
    telephone: '',
    prestataireMessage: '',
  }
}

// ── Singleton state (module-level, shared across all useDemande calls) ─────────

const etapeActuelle = ref<number>(1)
const direction = ref<'forward' | 'back'>('forward')
const submitted = ref(false)
const submitting = ref(false)
const submitError = ref<string | null>(null)
const state = reactive<DemandeState>(defaultDemandeState())

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
    Object.assign(state, defaultDemandeState())
    etapeActuelle.value = 1
    direction.value = 'forward'
    submitted.value = false
  }

  function initDemande(id: string, name: string, image: string, slug: string) {
    state.prestataireId = id
    state.prestataireName = name
    state.prestataireImage = image
    state.prestataireSlug = slug
  }

  async function submit() {
    submitting.value = true
    submitError.value = null
    try {
      const { localEvent, reset: resetLocalEvent } = useLocalEvent()

      const resolvedEventType =
        localEvent.eventType === 'autre' ? localEvent.eventTypeAutre || null : localEvent.eventType
      const resolvedAmbiance =
        localEvent.ambiance === 'autre' ? localEvent.ambianceAutre || null : localEvent.ambiance
      const resolvedMomentCle =
        localEvent.momentCle === 'autre' ? localEvent.momentCleAutre || null : localEvent.momentCle

      if (!state.prestataireId) {
        submitError.value = 'Prestataire manquant'
        return
      }

      const eventBody: DemandeRequest = {
        prestataireId: state.prestataireId,
        eventType: resolvedEventType,
        ambiance: resolvedAmbiance,
        momentCle: resolvedMomentCle,
        description: localEvent.description || null,
        date: localEvent.date ? toISODate(localEvent.date) : null,
        ville: localEvent.ville || null,
        nbInvites: localEvent.nbInvites || null,
        lieu: localEvent.lieu || null,
        prestataireMessage: state.prestataireMessage || null,
      }

      if (useFlow().currentFlow.value === 'new-event') {
        const { eventId } = await createEventApi(eventBody)
        resetLocalEvent() // matérialisé en base : le serveur devient la vérité
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
        resetLocalEvent() // transmis au serveur : plus de copie locale
        submitted.value = true
      }
    } catch (err) {
      console.error('submit error:', err)
      submitError.value = 'Une erreur est survenue. Veuillez réessayer.'
    } finally {
      submitting.value = false
    }
  }

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
  }
}
