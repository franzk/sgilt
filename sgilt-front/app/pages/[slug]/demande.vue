<template>
  <div v-if="noFlowWarning" class="no-flow">
    <p class="no-flow-title">{{ $t('tunnel.no-flow.title') }}</p>
    <p class="no-flow-message">{{ $t('tunnel.no-flow.message') }}</p>
    <NuxtLink to="/app">{{ $t('tunnel.no-flow.back-to-app') }}</NuxtLink>
  </div>

  <template v-else-if="prestataire">
    <!-- Visiteur non connecté sur mobile : récap de l'événement puis coordonnées ; la
         confirmation d'envoi est sur /demande-envoyee (desktop à venir). -->
    <template v-if="isMobile && isPublicVisitor">
      <DemandeCoordonnees
        v-if="publicStep === 'coordonnees' && summary"
        :summary="summary"
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
    <DemandeDesktop
      v-else-if="!isMobile"
      :slug="slug"
      :prestataire-name="prestataire.name"
      :prestataire-image="heroRef(prestataire.medias) ?? ''"
    />
    <DemandeMobile
      v-else
      :prestataire-name="prestataire.name"
      :prestataire-image="heroRef(prestataire.medias) ?? ''"
      :slug="slug"
    />
  </template>

  <div v-else-if="!loading" class="not-found">
    <p>{{ $t('provider.not-found') }}</p>
    <NuxtLink to="/search">{{ $t('provider.back-to-search') }}</NuxtLink>
  </div>
</template>

<script setup lang="ts">
import DemandeDesktop from '~/components/demande/DemandeDesktop.vue'
import DemandeMobile from '~/components/demande/DemandeMobile.vue'
import DemandeCoordonnees from '~/components/demande/DemandeCoordonnees.vue'
import DemandeRecapEvenement from '~/components/demande/DemandeRecapEvenement.vue'
import { useDemande } from '~/composables/useDemande'
import { usePrestataire } from '~/data/prestataire/usePrestataire'
import type { DemandeSummary } from '~/types/demande'
import { eventTypeKey } from '~/types/evenement'

const route = useRoute()
const slug = route.params.slug as string

const {
  state,
  confirmation,
  etapeActuelle,
  submitted,
  goTo,
  initDemande,
  submit,
  reset: resetDemande,
} = useDemande()
const { prestataire, loading } = usePrestataire(slug)

// Le prestataire visé vient de la route : la page le charge, la demande n'en garde qu'une référence.
watch(
  prestataire,
  (p) => {
    if (p) initDemande(p.id, p.name, heroRef(p.medias) ?? '', p.slug)
  },
  { immediate: true },
)

const { isMobile } = useDevice()

useHead({ title: 'Votre demande' })

const { localEvent, eventTypeLabel } = useLocalEvent()
const { categoryName, subcategoryName } = useCategories()
const { currentFlow } = useFlow()
const { isAuthenticated } = useKeycloak()

// Les flows connectés (new-event, add-prestataire) gardent le tunnel pas-à-pas.
const isPublicVisitor = computed(() => !isAuthenticated.value && currentFlow.value === null)

// Écran courant du parcours public dans l'URL (?etape=) : retour navigateur et refresh
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
// lien du mail. La confirmation est figée avant l'envoi, car submit() vide l'événement local en
// cas de succès ; la demande (coordonnées comprises) est vidée ensuite. En cas d'échec, l'erreur
// s'affiche sous le bouton. `replace` : le retour arrière ne ramène pas au formulaire.
async function onSend() {
  if (!summary.value) return
  const sentConfirmation = {
    summary: { ...summary.value, evenement: { ...summary.value.evenement } },
    email: state.email.trim(),
  }
  await submit()
  if (!submitted.value) return
  confirmation.value = sentConfirmation
  resetDemande()
  navigateTo('/demande-envoyee', { replace: true })
}

const noFlowWarning = ref(false)

onMounted(() => {
  if (currentFlow.value === null) {
    if (!isAuthenticated.value) {
      if (!localEvent.date) {
        // Si l'utilisateur n'est pas authentifié et que l'événement n'a pas de date,
        // on le redirige vers la page du prestataire pour qu'il puisse initier une demande
        navigateTo(`/${slug}`)
        return
      }
    } else {
      // Si l'utilisateur est authentifié mais qu'il n'y a pas de flow en cours, on affiche un message d'erreur
      noFlowWarning.value = true
      return
    }
  }

  if (
    etapeActuelle.value === 1 &&
    localEvent.eventType &&
    eventTypeKey(localEvent.eventType) !== 'autre'
  ) {
    // on saute l'étape de sélection du type d'événement si elle a déjà été saisie au début du parcours
    // et qu'elle n'est pas "Autre"
    goTo(2)
  }
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
