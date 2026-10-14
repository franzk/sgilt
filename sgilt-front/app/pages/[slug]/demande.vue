<template>
  <div v-if="noFlowWarning" class="no-flow">
    <p class="no-flow-title">{{ $t('tunnel.no-flow.title') }}</p>
    <p class="no-flow-message">{{ $t('tunnel.no-flow.message') }}</p>
    <NuxtLink to="/app">{{ $t('tunnel.no-flow.back-to-app') }}</NuxtLink>
  </div>

  <!-- Mêmes écrans pour tous, mobile comme desktop, visiteur comme connecté : récap de
       l'événement, puis envoi de la demande (sans les coordonnées pour un connecté). La
       confirmation d'envoi du visiteur est sur /demande-envoyee. -->
  <template v-else-if="prestataire">
    <DemandeCoordonnees
      v-if="publicStep === 'coordonnees' && summary"
      :summary="summary"
      :skip-contact="isNewEventFlow"
      @back="goToPublicStep('recap')"
      @send="onSend"
    />
    <DemandeRecapEvenement
      v-else
      :prestataire-name="prestataire.name"
      :prestataire-image="heroRef(prestataire.medias) ?? ''"
      :slug="slug"
      @continue="goToPublicStep('coordonnees')"
    />
  </template>

  <div v-else-if="!loading" class="not-found">
    <p>{{ $t('provider.not-found') }}</p>
    <NuxtLink to="/search">{{ $t('provider.back-to-search') }}</NuxtLink>
  </div>
</template>

<script setup lang="ts">
import DemandeCoordonnees from '~/components/demande/DemandeCoordonnees.vue'
import DemandeRecapEvenement from '~/components/demande/DemandeRecapEvenement.vue'
import { useDemande } from '~/composables/useDemande'
import { usePrestataire } from '~/data/prestataire/usePrestataire'
import type { DemandeSummary } from '~/types/demande'

const route = useRoute()
const slug = route.params.slug as string

const { state, confirmation, submitted, initDemande, submit, reset: resetDemande } = useDemande()
const { prestataire, loading } = usePrestataire(slug)

// Le prestataire visé vient de la route : la page le charge, la demande n'en garde qu'une référence.
watch(
  prestataire,
  (p) => {
    if (p) initDemande(p.id, p.name, heroRef(p.medias) ?? '', p.slug)
  },
  { immediate: true },
)

useHead({ title: 'Votre demande' })

const { localEvent, eventTypeLabel, reset: resetLocalEvent } = useLocalEvent()
const { categoryName, subcategoryName } = useCategories()
const { currentFlow, onFlowSuccess } = useFlow()
const { isAuthenticated } = useKeycloak()

// Nouvel événement d'un utilisateur connecté : les mêmes écrans que le visiteur (le prestataire a
// besoin des mêmes informations), sans les coordonnées, connues par le compte.
const isNewEventFlow = computed(() => currentFlow.value === 'new-event')

// Écran courant dans l'URL (?etape=) : retour navigateur et refresh
// fonctionnent sans état supplémentaire. Absent ou inconnu = récap de l'événement.
type PublicStep = 'recap' | 'coordonnees'
const publicStep = computed<PublicStep>(() =>
  route.query.etape === 'coordonnees' ? 'coordonnees' : 'recap',
)

// Le récap est l'écran par défaut : il n'a pas de paramètre dans l'URL.
async function goToPublicStep(step: PublicStep) {
  await navigateTo({ path: route.path, query: step === 'recap' ? {} : { etape: step } })
  window.scrollTo({ top: 0 })
}

// Synthèse prestataire + événement des écrans coordonnées et confirmation.
const summary = computed<DemandeSummary | null>(() => {
  const p = prestataire.value
  if (!p) return null
  return {
    prestataireName: p.name,
    prestataireImage: heroRef(p.medias) ?? '',
    // « Restauration · Traiteur » : catégorie puis sous-catégorie.
    prestataireCategoryLine: [categoryName(p.categoryKey), subcategoryName(p.subcatKey)].join(' · '),
    evenement: {
      eventTypeLabel: eventTypeLabel.value,
      date: localEvent.date,
      nbInvites: localEvent.nbInvites,
      ville: localEvent.ville,
    },
  }
})

// Envoi de la demande : le parcours public de cet événement est terminé, la suite passe par le
// lien du mail. L'événement local et la demande (coordonnées comprises) ne sont vidés qu'une fois
// sur l'écran de confirmation : vidés avant, les champs du formulaire s'effaceraient sous les yeux
// de l'utilisateur. En cas d'échec, l'erreur s'affiche sous le bouton. `replace` : le retour
// arrière ne ramène pas au formulaire.
async function onSend() {
  if (!summary.value) return
  // Connecté : l'événement est créé avec la demande, puis on arrive sur son board.
  if (isNewEventFlow.value) {
    await submit()
    if (!submitted.value) return
    resetDemande()
    onFlowSuccess()
    return
  }
  const sentConfirmation = {
    summary: { ...summary.value, evenement: { ...summary.value.evenement } },
    email: state.email.trim(),
  }
  await submit()
  if (!submitted.value) return
  confirmation.value = sentConfirmation
  await navigateTo('/demande-envoyee', { replace: true })
  resetLocalEvent()
  resetDemande()
}

const noFlowWarning = ref(false)

onMounted(() => {
  if (!isAuthenticated.value) {
    // Visiteur sans date d'événement : retour à la fiche, où la demande commence par la date.
    if (!localEvent.date) navigateTo(`/${slug}`)
    return
  }
  // Connecté hors flow « Nouvel événement » (l'ajout à un événement existant passe par la fenêtre
  // de finalisation de la fiche) : rien à envoyer d'ici.
  if (!isNewEventFlow.value) noFlowWarning.value = true
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.no-flow {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  gap: $spacing-m;
  padding: $spacing-l;
  text-align: center;

  .no-flow-title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.6rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0;
  }

  .no-flow-message {
    font-size: 0.95rem;
    color: $text-secondary;
    max-width: 400px;
    line-height: 1.6;
    margin: 0;
  }

  a {
    color: $color-primary;
    text-decoration: none;
    font-weight: 500;
  }
}

.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  gap: 1rem;
  font-size: 1rem;
  color: $text-secondary;

  a {
    color: $color-primary;
    text-decoration: none;
    font-weight: 500;
  }
}
</style>
