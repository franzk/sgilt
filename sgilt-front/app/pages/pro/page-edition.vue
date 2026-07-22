<template>
  <!-- Fiche entièrement vide : fork plein écran, aucun onglet n'a de sens tant qu'elle n'existe pas. -->
  <FicheOnboardingFork v-if="prestataire && showFork" @choose-manual="manualChosen = true" />

  <PrestataireDetails
    v-else-if="prestataire"
    :prestataire="prestataire"
    :display-mode="displayMode"
    @select="() => {}"
    @back="router.back()"
  >
    <template #tabs>
      <PageEditionTabs />
    </template>
    <!-- Onglet IA : rien n'appartenant à la fiche (pas de sidebar réservation/soumission, pas de
         contenu fiche) — remplace intégralement la zone de contenu par défaut. -->
    <template v-if="isIaTab" #content>
      <NuxtPage />
    </template>
    <template v-else #main>
      <NuxtPage />
    </template>
  </PrestataireDetails>
</template>

<script setup lang="ts">
import PrestataireDetails from '~/components/prestataire/PrestataireDetails.vue'
import PageEditionTabs from '~/components/prestataire/PageEditionTabs.vue'
import FicheOnboardingFork from '~/components/prestataire/FicheOnboardingFork.vue'
import type { DisplayMode } from '~/types/prestataire'

definePageMeta({ layout: 'default' })

const router = useRouter()
const route = useRoute()
const currentUser = useCurrentUser()
const { prestataire, loadMaFiche } = usePrestataire()

/**
 * Choix explicite de remplissage manuel : déverrouille les 3 onglets sans attendre qu'un champ
 * soit réellement rempli. Non persisté à dessein — un rechargement réévalue uniquement l'état de
 * la fiche, donc re-présente le fork tant qu'aucun champ n'est rempli.
 */
const manualChosen = ref(false)

const isEmptyFiche = computed(() => {
  const p = prestataire.value
  if (!p) return false
  return (
    !p.baseline &&
    !p.shortDescription &&
    p.offerings.length === 0 &&
    !p.identity.quote &&
    !p.identity.bio &&
    !p.budget &&
    p.testimonials.length === 0 &&
    p.details.length === 0 &&
    p.faq.length === 0
  )
})

const showFork = computed(() => isEmptyFiche.value && !manualChosen.value)

// L'onglet Preview affiche la fiche en lecture seule, chrome de réservation masqué
const displayMode = computed<DisplayMode>(() => {
  if (route.path === '/pro/page-edition/preview') return 'preview'
  if (route.path === '/pro/page-edition/ia') return 'IA'
  return 'edit'
})

const isIaTab = computed(() => displayMode.value === 'IA')

watch(
  () => currentUser.slug,
  (slug) => {
    if (slug) loadMaFiche()
  },
  { immediate: true },
)

// Aucune sous-route active (ex. arrivée directe sur /pro/page-edition) : l'onglet Édition
// est celui par défaut.
if (route.path === '/pro/page-edition') {
  await navigateTo('/pro/page-edition/edition', { replace: true })
}
</script>
