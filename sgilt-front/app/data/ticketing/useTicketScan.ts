/**
 * Composable — scan des e-billets à l'entrée d'un événement, pour son organisateur
 * (/app/events/[eventId]/scan, onglet Scan) : un QR lu est vérifié par le back, son issue reste
 * affichée jusqu'au QR suivant. Entrée validée → passage au suivant tout seul après
 * RESUME_DELAY_MS ; tout autre résultat attend next() (un refus ne doit pas disparaître pendant que
 * l'organisateur parle à la personne). Un QR lu pendant une vérification ou un affichage est ignoré.
 */
import type { ScanOutcome } from './domain/Scan'
import { scanEBillet } from './service/scanService'

// Entrée validée : délai avant de reprendre la lecture tout seul.
const RESUME_DELAY_MS = 2000

// onValidated : appelé à chaque entrée validée (ex. recharger les compteurs).
export function useTicketScan(eventId: string, onValidated: () => void) {
  // Issue du dernier scan, affichée ; null : prêt à lire le QR suivant.
  const outcome = ref<ScanOutcome | null>(null)
  // Vérification en cours.
  const checking = ref(false)
  let resumeTimer: ReturnType<typeof setTimeout> | undefined

  async function scan(qrContent: string) {
    if (checking.value || outcome.value) return
    checking.value = true
    let result: ScanOutcome
    try {
      result = await scanEBillet(eventId, qrContent)
    } catch {
      result = { status: 'FAILED', commande: null }
    } finally {
      checking.value = false
    }
    outcome.value = result
    if (result.status === 'VALIDATED') {
      onValidated()
      resumeTimer = setTimeout(next, RESUME_DELAY_MS)
    }
  }

  // Efface l'issue affichée : prêt pour le QR suivant.
  function next() {
    clearTimeout(resumeTimer)
    outcome.value = null
  }

  onScopeDispose(() => clearTimeout(resumeTimer))

  return { outcome, checking, scan, next }
}
