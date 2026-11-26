/**
 * Composable — une liste de réservations du prestataire connecté (résumés), chargée par `fetcher`
 * dès qu'il est authentifié et que `enabled` est vrai. Une fois demandée, la liste n'est pas
 * rechargée (sauf échec). Utilisé par les onglets Réservations (à traiter, puis toutes à la
 * demande) et Calendrier (confirmées) de l'espace pro.
 */
import type { ProReservationSummary } from './domain/ProReservationSummary'

export function useProReservations(
  fetcher: () => Promise<ProReservationSummary[]>,
  enabled: () => boolean = () => true,
) {
  const { isAuthenticated } = useKeycloak()
  const reservations = ref<ProReservationSummary[]>([])
  const loading = ref(true)
  // Chargement demandé (en cours ou réussi) : évite un double appel si le watch se redéclenche.
  let requested = false
  const error = ref<unknown>(null)

  async function load() {
    requested = true
    loading.value = true
    error.value = null
    try {
      reservations.value = await fetcher()
    } catch (e) {
      error.value = e
      requested = false
    } finally {
      loading.value = false
    }
  }

  watch(
    [isAuthenticated, enabled],
    ([authenticated, isEnabled]) => {
      if (authenticated && isEnabled && !requested) load()
    },
    { immediate: true },
  )

  return { reservations, loading, error }
}
