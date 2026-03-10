// app/composables/useDemande.ts

import {
  type DemandeState,
  EVENT_TYPE_OPTIONS,
  AMBIANCE_OPTIONS,
  MOMENT_CLE_OPTIONS,
} from '~/types/demande'

export function useDemande(initialDate?: Date | null | undefined) {
  const etapeActuelle = ref(1)
  const direction = ref<'forward' | 'back'>('forward')
  const submitted = ref(false)
  const submitting = ref(false)

  const state = reactive<DemandeState>({
    eventType: null,
    eventTypeAutre: '',
    ambiance: null,
    ambianceAutre: '',
    momentCle: null,
    momentCleAutre: '',
    description: '',
    date: initialDate ?? undefined,
    ville: '',
    nbInvites: '',
    lieuDefini: true,
    lieu: '',
    email: '',
    telephone: '',
    prestataireMessage: '',
  })

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

  async function submit() {
    submitting.value = true
    // TODO: remplacer par un vrai appel API / email
    await new Promise((resolve) => setTimeout(resolve, 800))
    console.log('Demande envoyée:', toRaw(state))
    submitting.value = false
    submitted.value = true
  }

  // ─── Helpers labels pour le récap ─────────────────────────────────────────

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
    state,
    next,
    back,
    goTo,
    submit,
    eventTypeLabel,
    eventTypeEmoji,
    ambianceLabel,
    ambianceEmoji,
    momentCleLabel,
    momentCleEmoji,
  }
}
