/**
 * Composable — commandes de la billetterie d'un événement, pour son organisateur (onglet Commandes
 * de /app/events/[eventId]/billetterie, onglet Récap de /app/events/[eventId]/scan) : toutes, quel
 * que soit leur statut, les plus récentes d'abord ; le filtre est fait par l'écran.
 */
import type { OrganizerCommande } from './domain/Commande'
import { fetchOrganizerCommandes } from './service/organizerPageService'

// État propre à chaque appel : chaque écran charge sa liste.
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
