<!--
  SCAN des e-billets d'un événement par son organisateur, à l'entrée : écran plein écran (ni
  en-tête, ni navigation, ni sidebar du board), ouvert depuis la billetterie. Exemple : événement 42.
    /app/events/42/scan         onglet Scan (lecteur de QR)
    /app/events/42/scan/recap   onglet Récap (les commandes, comme l'onglet Commandes de la billetterie)
  [[onglet]] (doubles crochets) = segment facultatif ; un segment inconnu ramène sur Scan. La croix
  ramène à la billetterie. N'existe que si le back-office a activé la billetterie (sinon, retour au board).
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
      <p v-if="tab === 'scan'" class="placeholder">{{ $t('evenement.scan.placeholder') }}</p>

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
import ScanTabs from '~/components/evenement/ScanTabs.vue'
import { useOrganizerCommandes } from '~/data/ticketing/useOrganizerCommandes'
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

const {
  commandes,
  loading: commandesLoading,
  loadFailed: commandesLoadFailed,
  load: loadCommandes,
} = useOrganizerCommandes(eventId)

// Les commandes sont rechargées à chaque ouverture du Récap : il suit les entrées de la soirée.
function refresh() {
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

    .placeholder {
      margin: 0;
      padding: $spacing-m;
      border-radius: $radius-lg;
      background: $surface-soft;
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }
}
</style>
