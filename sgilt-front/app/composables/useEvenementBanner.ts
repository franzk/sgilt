// Bandeau « événement en cours de création » : visible sur toutes les pages publiques
// (layouts default et evenement) dès que la partie publique est en mode événement, c'est-à-dire
// que le visiteur a choisi de commencer son organisation (localEvent.organisationStarted), afin
// de pouvoir toujours retrouver son événement. Les utilisateurs connectés ont leurs propres
// flows (ContextBanner).
export function useEvenementBanner() {
  const { isAuthenticated, isInitialized } = useKeycloak()
  const { localEvent } = useLocalEvent()
  const { showContextBanner } = useFlow()

  // Affiché après montage : l'événement local et Keycloak n'existent que côté client,
  // le rendu serveur et le premier rendu client doivent rester identiques.
  const mounted = ref(false)
  onMounted(() => {
    mounted.value = true
  })

  const visible = computed(
    () =>
      mounted.value &&
      isInitialized.value &&
      !isAuthenticated.value &&
      !showContextBanner.value &&
      localEvent.organisationStarted,
  )

  return { visible }
}
