import type {
  DemandeConfirmation,
  DemandeRequest,
  OnboardingDemandeRequest,
  DemandeState,
} from '~/types/demande'
import { submitOnboarding } from '~/data/onboarding/api/onboardingApi'

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

const submitted = ref(false)
const submitting = ref(false)
const submitError = ref<string | null>(null)
const state = reactive<DemandeState>(defaultDemandeState())
// Confirmation de la dernière demande envoyée (écran /demande-envoyee). Hors de reset() : la
// demande est vidée à l'envoi, la confirmation doit lui survivre. Perdue au rafraîchissement.
const confirmation = ref<DemandeConfirmation | null>(null)

// ── Composable ────────────────────────────────────────────────────────────────

export function useDemande() {
  function reset() {
    Object.assign(state, defaultDemandeState())
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
      const { toEvenementRequest, reset: resetLocalEvent, create } = useLocalEvent()

      if (!state.prestataireId) {
        submitError.value = 'Prestataire manquant'
        return
      }

      // Connecté (flow « Nouvel événement ») : le back crée l'événement avec cette première
      // demande ; les coordonnées transmises au prestataire sont celles du compte.
      if (useFlow().currentFlow.value === 'new-event') {
        const demande = {
          prestataireId: state.prestataireId,
          message: state.prestataireMessage || null,
        }
        useFlow().flowPayload.value = { id: await create(demande) }
        submitted.value = true
        return
      }

      const demandeBody: DemandeRequest = {
        ...toEvenementRequest(),
        prestataireId: state.prestataireId,
        prestataireMessage: state.prestataireMessage || null,
      }

      const onboardingBody: OnboardingDemandeRequest = {
        ...demandeBody,
        firstName: state.prenom,
        lastName: state.nom,
        email: state.email,
        telephone: state.telephone || null,
      }
      await submitOnboarding(onboardingBody)
      resetLocalEvent() // transmis au serveur : plus de copie locale
      submitted.value = true
    } catch (err) {
      console.error('submit error:', err)
      submitError.value = 'Une erreur est survenue. Veuillez réessayer.'
    } finally {
      submitting.value = false
    }
  }

  return {
    submitted,
    submitting,
    submitError,
    state,
    confirmation,
    initDemande,
    reset,
    submit,
  }
}
