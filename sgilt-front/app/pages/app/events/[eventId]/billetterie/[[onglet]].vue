<!--
  BILLETTERIE d'un événement administrée par son organisateur, ouverte depuis la sidebar (desktop)
  ou la carte de l'accueil du board (mobile). Exemple : événement 42.
    /app/events/42/billetterie             onglet Réglages (statut, prix, jauge, avec leur bouton)
    /app/events/42/billetterie/commandes   onglet Commandes (liste filtrable par statut)
  [[onglet]] (doubles crochets) = segment facultatif ; un segment inconnu ramène sur Réglages.
  Au-dessus des onglets, toujours visibles : les ventes (si la billetterie existe). N'existe que si
  le back-office a activé la billetterie (sinon, retour au board).
-->
<template>
  <div class="billetterie">
    <div class="column">
      <NuxtLink :to="boardPath" class="back">
        <ArrowLeftIcon class="icon" aria-hidden="true" />
        {{ $t('evenement.billetterie.back') }}
      </NuxtLink>

      <h1 class="title">{{ $t('evenement.billetterie.title') }}</h1>

      <Sk v-if="loading" class="skeleton" />

      <div v-else-if="loadFailed" class="load-error" role="alert">
        <p>{{ $t('evenement.billetterie.errors.LOAD') }}</p>
        <SgiltButton variant="secondary" @click="load">{{ $t('evenement.billetterie.retry') }}</SgiltButton>
      </div>

      <template v-else-if="publicPage?.billetterieEnabled">
        <PublicPageSalesBlock v-if="publicPage.sales" :sales="publicPage.sales" />

        <BilletterieTabs v-if="tab" :billetterie-path="billetteriePath" :active="tab" />

        <PublicPageTicketingBlock
          v-if="tab === 'reglages'"
          :ticketing="publicPage.ticketing"
          :sold-tickets="publicPage.sales?.soldTickets ?? 0"
          :page-published="!!publicPage.page?.published"
          :saving="savingTicketing"
          :saved="ticketingSaved"
          :failure="ticketingFailure"
          @save="saveTicketing"
          @fail="failTicketing"
        />

        <BilletterieCommandesBlock
          v-else-if="tab === 'commandes'"
          :commandes="commandes"
          :loading="commandesLoading"
          :load-failed="commandesLoadFailed"
          @retry="loadCommandes"
        />
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
// La billetterie est chargée avec la page publique dont elle dépend (même appel, même état).
import { ArrowLeftIcon } from '@remixicons/vue/line'
import Sk from '~/components/basics/Sk.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import BilletterieCommandesBlock from '~/components/evenement/BilletterieCommandesBlock.vue'
import BilletterieTabs from '~/components/evenement/BilletterieTabs.vue'
import PublicPageSalesBlock from '~/components/evenement/PublicPageSalesBlock.vue'
import PublicPageTicketingBlock from '~/components/evenement/PublicPageTicketingBlock.vue'
import { useOrganizerCommandes } from '~/data/ticketing/useOrganizerCommandes'
import { useOrganizerPublicPage } from '~/data/ticketing/useOrganizerPublicPage'
import { useEventContext } from '~/data/evenement/useEventContext'
import { billetterieTabOf } from '~/constants/billetterie-tabs'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
useHead({ title: t('evenement.billetterie.page-title') })

const route = useRoute()
const { eventId } = useEventContext()
const boardPath = `/app/events/${eventId}`
const billetteriePath = `${boardPath}/billetterie`

// Segment après la billetterie : un onglet, ou rien (Réglages). Inconnu (URL tapée à la main) :
// retour sur Réglages.
const segment = computed(() =>
  typeof route.params.onglet === 'string' ? route.params.onglet : undefined,
)
const tab = computed(() => billetterieTabOf(segment.value))
watch(
  segment,
  () => {
    if (!tab.value) navigateTo(billetteriePath, { replace: true })
  },
  { immediate: true },
)

const {
  publicPage,
  loading,
  loadFailed,
  notFound,
  savingTicketing,
  ticketingSaved,
  ticketingFailure,
  load,
  saveTicketing,
  failTicketing,
} = useOrganizerPublicPage(eventId)

const {
  commandes,
  loading: commandesLoading,
  loadFailed: commandesLoadFailed,
  load: loadCommandes,
} = useOrganizerCommandes(eventId)

// Tout est rechargé à chaque ouverture d'un onglet : ventes, réglages et commandes restent cohérents.
// Segment inconnu : rien, la redirection vers Réglages rechargera.
function refresh() {
  if (!tab.value) return
  load()
  if (tab.value === 'commandes') loadCommandes()
}
onMounted(refresh)
watch(tab, refresh)

// Page publique ou billetterie pas (ou plus) activée par le back-office : rien à administrer ici.
watch([notFound, publicPage], ([pageNotFound, page]) => {
  if (pageNotFound || (page && !page.billetterieEnabled)) navigateTo(boardPath, { replace: true })
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Même mise en page que l'écran Page publique.
.billetterie {
  display: flex;
  justify-content: center;
  flex: 1;
  background: $surface-white;

  .column {
    display: flex;
    flex-direction: column;
    gap: $spacing-l;
    width: 100%;
    max-width: 30rem;
    padding: $spacing-m $section-padding-x $spacing-xl;

    .back {
      display: inline-flex;
      align-items: center;
      align-self: flex-start;
      gap: $spacing-xxs;
      color: $text-primary;
      font-size: $font-size-sm;
      text-decoration: none;

      .icon {
        width: 1.125rem;
        height: 1.125rem;
      }
    }

    .title {
      margin: 0;
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.75rem;
      font-weight: 600;
      color: $brand-primary;
    }

    .skeleton {
      height: 12rem;
      border-radius: $radius-lg;
    }

    .load-error {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      gap: $spacing-s;

      p {
        margin: 0;
        font-size: $font-size-sm;
        color: $text-primary;
      }
    }
  }
}
</style>
