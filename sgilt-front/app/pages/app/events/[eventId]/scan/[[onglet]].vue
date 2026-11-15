<!--
  SCAN des e-billets d'un événement par son organisateur, à l'entrée : écran plein écran (ni
  en-tête, ni navigation, ni sidebar du board), ouvert depuis la billetterie. Exemple : événement 42.
    /app/events/42/scan         onglet Scan (places entrées / vendues, lecteur de QR, résultat du scan)
    /app/events/42/scan/recap   onglet Récap (les commandes, comme l'onglet Commandes de la billetterie)
  [[onglet]] (doubles crochets) = segment facultatif ; un segment inconnu ramène sur Scan. La croix
  ramène à la billetterie. N'existe que si le back-office a activé la billetterie (sinon, retour au board).
  Scan : un QR lu fige la caméra et affiche le résultat par-dessus ; entrée validée → QR suivant
  tout seul après RESUME_DELAY_MS, sinon au toucher « Scanner le suivant » (un refus ne doit pas
  disparaître pendant que l'organisateur parle à la personne).
-->
<template>
  <div class="scan-screen">
    <header class="header">
      <div class="titles">
        <h1 class="title">{{ $t('evenement.scan.title') }}</h1>
        <p v-if="event" class="event-title">{{ event.title }}</p>
      </div>
      <NuxtLink :to="billetteriePath" class="close" :aria-label="$t('evenement.scan.close')">
        <CloseLargeIcon class="icon" aria-hidden="true" />
      </NuxtLink>
    </header>

    <ScanTabs v-if="tab" :scan-path="scanPath" :active="tab" />

    <div class="content">
      <div v-if="tab === 'scan'" class="scan">
        <p v-if="publicPage?.sales" class="counters">
          {{ $t('evenement.scan.counters', { entered: enteredTickets, sold: publicPage.sales.soldTickets }) }}
        </p>

        <div class="stage">
          <TicketScanner :paused="checking || showsResult" @detect="onDetect" />
          <ScanResultCard
            v-if="showsResult"
            class="result"
            :result="result"
            :failed="scanFailed"
            @next="next"
          />
        </div>
      </div>

      <BilletterieCommandesBlock
        v-else-if="tab === 'recap'"
        :commandes="commandes"
        :loading="commandesLoading"
        :load-failed="commandesLoadFailed"
        @retry="loadCommandes"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { CloseLargeIcon } from '@remixicons/vue/line'
import BilletterieCommandesBlock from '~/components/evenement/BilletterieCommandesBlock.vue'
import ScanResultCard from '~/components/evenement/ScanResultCard.vue'
import ScanTabs from '~/components/evenement/ScanTabs.vue'
import TicketScanner from '~/components/evenement/TicketScanner.vue'
import { useOrganizerCommandes } from '~/data/ticketing/useOrganizerCommandes'
import { useOrganizerPublicPage } from '~/data/ticketing/useOrganizerPublicPage'
import type { ScanResult } from '~/data/ticketing/domain/Scan'
import { scanEBillet } from '~/data/ticketing/service/scanService'
import { useEventContext } from '~/data/evenement/useEventContext'
import { scanTabOf } from '~/constants/scan-tabs'

// Plein écran : pas de layout, et la route parente masque la sidebar (fullscreen).
definePageMeta({ layout: false, fullscreen: true })

const { t } = useI18n()
useHead({ title: t('evenement.scan.page-title') })

const route = useRoute()
const { eventId, event } = useEventContext()
const boardPath = `/app/events/${eventId}`
const billetteriePath = `${boardPath}/billetterie`
const scanPath = `${boardPath}/scan`

// Segment après le scan : un onglet, ou rien (Scan). Inconnu (URL tapée à la main) : retour sur Scan.
const segment = computed(() =>
  typeof route.params.onglet === 'string' ? route.params.onglet : undefined,
)
const tab = computed(() => scanTabOf(segment.value))
watch(
  segment,
  () => {
    if (!tab.value) navigateTo(scanPath, { replace: true })
  },
  { immediate: true },
)

// Entrée validée : délai avant de reprendre la lecture tout seul.
const RESUME_DELAY_MS = 2000

// Les ventes (places vendues) viennent de la billetterie, comme sur son écran.
const { publicPage, load: loadPublicPage } = useOrganizerPublicPage(eventId)

// ⚠ PROVISOIRE (brique F3, service simulé) : places entrées comptées par cette page ; viendront des
// ventes de la billetterie avec le back (brique B2).
const enteredTickets = ref(0)

const result = ref<ScanResult | null>(null)
// Vérification impossible (réseau, serveur) : affichée comme un refus, à rescanner.
const scanFailed = ref(false)
// Vérification en cours : la caméra est figée, un autre QR lu est ignoré.
const checking = ref(false)
const showsResult = computed(() => !!result.value || scanFailed.value)
let resumeTimer: ReturnType<typeof setTimeout> | undefined

async function onDetect(qrContent: string) {
  if (checking.value || showsResult.value) return
  checking.value = true
  try {
    result.value = await scanEBillet(eventId, qrContent)
  } catch {
    scanFailed.value = true
  } finally {
    checking.value = false
  }
  if (result.value?.status === 'VALIDATED') {
    enteredTickets.value += result.value.commande?.quantity ?? 0
    resumeTimer = setTimeout(next, RESUME_DELAY_MS)
  }
}

// Efface le résultat : la caméra reprend la lecture.
function next() {
  clearTimeout(resumeTimer)
  result.value = null
  scanFailed.value = false
}
onBeforeUnmount(() => clearTimeout(resumeTimer))

const {
  commandes,
  loading: commandesLoading,
  loadFailed: commandesLoadFailed,
  load: loadCommandes,
} = useOrganizerCommandes(eventId)

// Rechargés à chaque ouverture d'un onglet : les ventes pour le Scan, les commandes pour le Récap
// (il suit les entrées de la soirée).
function refresh() {
  if (tab.value === 'scan') loadPublicPage()
  if (tab.value === 'recap') loadCommandes()
}
onMounted(refresh)
watch(tab, refresh)

// Billetterie pas (ou plus) activée par le back-office : rien à scanner ici.
watch(
  event,
  (loaded) => {
    if (loaded && !loaded.billetterieEnabled) navigateTo(boardPath, { replace: true })
  },
  { immediate: true },
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.scan-screen {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 100dvh;
  padding-top: env(safe-area-inset-top, 0px);
  padding-bottom: env(safe-area-inset-bottom, 0px);
  background: $surface-white;

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-s;
    padding: $spacing-s $section-padding-x;

    .titles {
      min-width: 0;

      .title {
        margin: 0;
        font-family: 'Cormorant Garamond', serif;
        font-size: 1.5rem;
        font-weight: 600;
        color: $brand-primary;
      }

      .event-title {
        margin: 0;
        font-size: $font-size-sm;
        color: $text-secondary;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .close {
      display: flex;
      flex-shrink: 0;
      align-items: center;
      justify-content: center;
      width: 2.75rem;
      height: 2.75rem;
      border-radius: 9999px;
      color: $text-primary;

      .icon {
        width: 1.5rem;
        height: 1.5rem;
      }
    }
  }

  .content {
    display: flex;
    flex-direction: column;
    flex: 1;
    width: 100%;
    max-width: 30rem;
    margin: 0 auto;
    padding: $spacing-m $section-padding-x $spacing-xl;

    .scan {
      display: flex;
      flex-direction: column;
      gap: $spacing-m;

      .counters {
        margin: 0;
        font-size: 1.125rem;
        font-weight: 600;
        color: $text-primary;
        text-align: center;
      }

      // Le résultat recouvre la caméra figée, à la même taille.
      .stage {
        position: relative;

        .result {
          position: absolute;
          inset: 0;
        }
      }
    }
  }
}
</style>
