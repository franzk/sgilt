<!--
  Billetterie d'un événement administrée par son organisateur (/app/events/{eventId}/billetterie),
  ouverte depuis la sidebar (desktop) ou la carte de l'accueil du board (mobile). N'existe que si le
  back-office a activé la billetterie (sinon, retour au board). De haut en bas : ventes (si la
  billetterie existe), puis ses réglages (statut, prix, jauge) avec leur bouton.
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

        <PublicPageTicketingBlock
          :ticketing="publicPage.ticketing"
          :sold-tickets="publicPage.sales?.soldTickets ?? 0"
          :page-published="!!publicPage.page?.published"
          :saving="savingTicketing"
          :saved="ticketingSaved"
          :failure="ticketingFailure"
          @save="saveTicketing"
          @fail="failTicketing"
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
import PublicPageSalesBlock from '~/components/evenement/PublicPageSalesBlock.vue'
import PublicPageTicketingBlock from '~/components/evenement/PublicPageTicketingBlock.vue'
import { useOrganizerPublicPage } from '~/data/ticketing/useOrganizerPublicPage'
import { useEventContext } from '~/data/evenement/useEventContext'

definePageMeta({ layout: 'app' })

const { t } = useI18n()
useHead({ title: t('evenement.billetterie.page-title') })

const { eventId } = useEventContext()
const boardPath = `/app/events/${eventId}`

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

onMounted(load)

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
