/**
 * Composable — commandes de la billetterie d'un événement, pour son organisateur
 * (/app/events/[eventId]/billetterie) : toutes, quel que soit leur statut, les plus récentes
 * d'abord ; le filtre par statut est fait par l'écran.
 */
import type { OrganizerCommande } from './domain/Commande'
import { fetchOrganizerCommandes } from './service/organizerPageService'

// État propre à chaque appel (un seul écran l'utilise).
export function useOrganizerCommandes(eventId: string) {
  const commandes = ref<OrganizerCommande[]>([])
  const loading = ref(true)
  const loadFailed = ref(false)

  async function load() {
    loading.value = true
    loadFailed.value = false
    try {
      commandes.value = await fetchOrganizerCommandes(eventId)
    } catch {
      loadFailed.value = true
    } finally {
      loading.value = false
    }
  }

  return { commandes, loading, loadFailed, load }
}
