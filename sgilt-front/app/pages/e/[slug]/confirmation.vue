<!--
  Étape 3 du parcours d'achat (/e/[slug]/confirmation?commande={id}) : confirmation de l'achat,
  récapitulatif des billets et informations pratiques. La commande vient du back, relue toutes les
  2 s tant qu'elle n'est pas payée : Stripe confirme le paiement par webhook, quelques secondes
  après le retour du widget. Au bout de 30 s, un message rassurant remplace l'attente.
-->
<template>
  <div class="confirmation-page">
    <div v-if="!paidCommande" class="pending-state" role="status">
      <template v-if="pollTimedOut">
        <span class="pending-icon" aria-hidden="true"><TimeIcon /></span>
        <h1 class="pending-title">{{ $t('ticketing.confirmation.pending-timeout-title') }}</h1>
        <p class="pending-subtitle">{{ $t('ticketing.confirmation.pending-timeout-subtitle') }}</p>
      </template>
      <template v-else>
        <span class="spinner" aria-hidden="true" />
        <h1 class="pending-title">{{ $t('ticketing.confirmation.pending-title') }}</h1>
      </template>
    </div>

    <template v-else>
      <div class="success-hero">
        <span class="success-icon" aria-hidden="true">
          <CheckboxCircleIcon />
        </span>
        <h1 class="success-title">{{ $t('ticketing.confirmation.title') }}</h1>
        <p class="success-subtitle">{{ $t('ticketing.confirmation.subtitle') }}</p>
      </div>

      <TicketingEventRecap :event="event" />

      <section class="tickets-card">
        <div class="tickets-header">
          <h2 class="tickets-title">{{ $t('ticketing.confirmation.tickets-title') }}</h2>
          <span class="confirmed-badge">
            <CheckIcon class="icon" />
            {{ $t('ticketing.confirmation.confirmed-badge') }}
          </span>
        </div>
        <div class="tickets-row">
          <span class="label">{{ $t('ticketing.confirmation.quantity-label') }}</span>
          <span class="value">{{ paidCommande.quantity }}</span>
        </div>
        <div class="tickets-row">
          <span class="label">{{ $t('ticketing.confirmation.unit-price-label') }}</span>
          <span class="value">{{ unitPriceLabel }}</span>
        </div>
        <div class="total-row">
          <span class="label">{{ $t('ticketing.event.total-label') }}</span>
          <span class="value">{{ totalLabel }}</span>
        </div>
      </section>

      <button type="button" class="primary-cta" @click="onViewEBillet">
        <TicketIcon class="icon" />
        {{ $t('ticketing.confirmation.view-e-billet') }}
        <ArrowRightSIcon class="chevron" aria-hidden="true" />
      </button>

      <button type="button" class="secondary-cta" @click="onAddToCalendar">
        <CalendarEventIcon class="icon" />
        {{ $t('ticketing.confirmation.add-to-calendar') }}
        <ArrowRightSIcon class="chevron" aria-hidden="true" />
      </button>

      <div v-if="event.practicalInfo.length" class="practical-info">
        <span class="info-badge" aria-hidden="true"><InformationIcon /></span>
        <div>
          <p class="info-title">{{ $t('ticketing.confirmation.practical-info-title') }}</p>
          <ul>
            <li v-for="line in event.practicalInfo" :key="line">{{ line }}</li>
          </ul>
        </div>
      </div>

      <NuxtLink :to="`/e/${slug}`" class="secondary-cta back-link">
        <ArrowLeftSIcon class="icon" aria-hidden="true" />
        {{ $t('ticketing.confirmation.back-to-event') }}
      </NuxtLink>
    </template>
  </div>
</template>

<script setup lang="ts">
import TicketingEventRecap from '~/components/ticketing/TicketingEventRecap.vue'
import {
  CheckboxCircleIcon,
  CheckIcon,
  TicketIcon,
  CalendarEventIcon,
  InformationIcon,
  ArrowLeftSIcon,
  ArrowRightSIcon,
  TimeIcon,
} from '@remixicons/vue/line'
import { usePublicEvent } from '~/composables/usePublicEvent'
import { getCommande, isCommandeNotFound } from '~/data/ticketing/service/commandeService'
import type { CommandeSummary } from '~/data/ticketing/domain/Commande'
import { buildIcsEvent, downloadIcs } from '~/utils/icsUtils'
import { formatPrice } from '~/utils/priceUtils'
import { placeLabel } from '~/utils/placeUtils'

definePageMeta({ middleware: 'ticketing' })

const { t } = useI18n()
const route = useRoute()
const slug = String(route.params.slug)
const event = await usePublicEvent(slug)
// Garanti non vide par le middleware `ticketing`.
const commandeId = String(route.query.commande)

useHead({ title: `${t('ticketing.confirmation.title')} · ${event.title}` })

// ── Attente du paiement ──
const POLL_INTERVAL_MS = 2000
const POLL_TIMEOUT_MS = 30000

// La commande, une fois payée ; null tant qu'elle ne l'est pas.
const paidCommande = ref<CommandeSummary | null>(null)
const pollTimedOut = ref(false)
let pollTimer: ReturnType<typeof setTimeout> | undefined
let pollStartedAt = 0

// Relit la commande jusqu'à PAID, ou jusqu'au délai d'attente. Une erreur passagère ne coupe pas
// l'attente ; une commande inconnue renvoie à la fiche de l'événement.
async function pollCommande(): Promise<void> {
  try {
    const commande = await getCommande(commandeId)
    if (commande.status === 'PAID') {
      paidCommande.value = commande
      return
    }
  } catch (error) {
    if (isCommandeNotFound(error)) {
      await navigateTo(`/e/${slug}`, { replace: true })
      return
    }
  }
  if (Date.now() - pollStartedAt >= POLL_TIMEOUT_MS) {
    pollTimedOut.value = true
    return
  }
  pollTimer = setTimeout(pollCommande, POLL_INTERVAL_MS)
}

onMounted(() => {
  pollStartedAt = Date.now()
  pollCommande()
})

onBeforeUnmount(() => clearTimeout(pollTimer))

// Montants de la commande payée, convertis en euros pour l'affichage seulement.
const unitPriceLabel = computed(() => formatPrice((paidCommande.value?.unitPriceCents ?? 0) / 100))
const totalLabel = computed(() => formatPrice((paidCommande.value?.totalCents ?? 0) / 100))

function onViewEBillet(): void {
  navigateTo({ path: `/e/${slug}/e-billet`, query: { commande: commandeId } })
}

function onAddToCalendar(): void {
  const ics = buildIcsEvent({
    uid: `${slug}-${event.date}@sgilt.alsace`,
    title: event.title,
    description: event.subtitle ?? '',
    location: placeLabel(event.venue, event.city, ', '),
    url: `${window.location.origin}/e/${slug}`,
    date: event.date,
    startsAt: event.startsAt,
    endsAt: event.endsAt,
  })
  downloadIcs(`${slug}.ics`, ics)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.confirmation-page {
  width: 100%;
  max-width: 640px;
  margin: 0 auto;
  padding: $spacing-l $spacing-m;
  padding-bottom: $spacing-xxl;
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
}

// ─── Attente du paiement ──────────────────────────────────────────────────────
.pending-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-s;
  text-align: center;
  padding: $spacing-xl 0;

  .spinner {
    width: 2.5rem;
    height: 2.5rem;
    border-radius: 50%;
    border: 3px solid $brand-primary;
    border-top-color: transparent;
    animation: spin 0.8s linear infinite;
    margin-bottom: $spacing-xs;
  }

  .pending-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 4.5rem;
    height: 4.5rem;
    border-radius: 50%;
    background: $surface-soft;
    color: $brand-primary;
    margin-bottom: $spacing-xs;

    svg {
      width: 2.5rem;
      height: 2.5rem;
    }
  }

  .pending-title {
    margin: 0;
    font-size: $font-size-xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .pending-subtitle {
    margin: 0;
    max-width: 26rem;
    font-size: $font-size-md;
    line-height: $line-height-relaxed;
    color: $text-secondary;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

// ─── Bloc succès ──────────────────────────────────────────────────────────────
.success-hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-s;
  text-align: center;
  padding: $spacing-m 0;
}

.success-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 4.5rem;
  height: 4.5rem;
  border-radius: 50%;
  background: rgba($state-success, 0.12);
  color: $state-success;
  margin-bottom: $spacing-xs;

  svg {
    width: 2.5rem;
    height: 2.5rem;
  }
}

.success-title {
  margin: 0;
  font-size: $font-size-xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.success-subtitle {
  margin: 0;
  max-width: 26rem;
  font-size: $font-size-md;
  line-height: $line-height-relaxed;
  color: $text-secondary;
}

// ─── Carte billets ────────────────────────────────────────────────────────────
.tickets-card {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m;
  border: 1px solid $divider-color;
  border-radius: $radius-lg;
}

.tickets-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;
  padding-bottom: $spacing-m;
  border-bottom: 1px solid $divider-color;
}

.tickets-title {
  margin: 0;
  font-size: $font-size-lg;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.confirmed-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  flex-shrink: 0;
  padding: 0.35rem 0.8rem;
  border-radius: 2rem;
  background: rgba($state-success, 0.12);
  color: $state-success;
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  white-space: nowrap;

  .icon {
    width: 1rem;
    height: 1rem;
  }
}

.tickets-row {
  display: flex;
  align-items: center;
  justify-content: space-between;

  .label {
    font-size: $font-size-md;
    color: $text-secondary;
  }

  .value {
    font-size: $font-size-md;
    font-weight: $font-weight-semibold;
    color: $text-primary;
  }
}

.total-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: $spacing-s;
  border-top: 1px solid $divider-color;

  .label {
    font-size: $font-size-lg;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .value {
    font-size: $font-size-2xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }
}

// ─── CTA ─────────────────────────────────────────────────────────────────────
.primary-cta,
.secondary-cta {
  width: 100%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5em;
  height: 3.25rem;
  border-radius: 9999px;
  font-family: inherit;
  font-size: $font-size-md;
  font-weight: $font-weight-bold;
  cursor: pointer;
  text-decoration: none;

  .icon {
    width: 1.1rem;
    height: 1.1rem;
  }

  .chevron {
    width: 1.1rem;
    height: 1.1rem;
  }
}

.primary-cta {
  border: none;
  background: $brand-accent;
  color: $brand-primary;
  box-shadow: 0 4px 10px rgba($brand-primary, 0.18);
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba($brand-primary, 0.22);
  }

  &:focus-visible {
    outline: 3px solid $brand-primary;
    outline-offset: 4px;
  }
}

.secondary-cta {
  border: 1px solid $divider-color;
  background: $surface-white;
  color: $text-primary;
  transition: background 150ms ease;

  &:hover {
    background: $surface-soft;
  }
}

.back-link {
  font-weight: $font-weight-semibold;
}

// ─── Informations pratiques ───────────────────────────────────────────────────
.practical-info {
  display: flex;
  align-items: flex-start;
  gap: $spacing-s;
  padding: $spacing-m;
  background: $surface-soft;
  border-radius: $radius-md;

  .info-badge {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 1.75rem;
    height: 1.75rem;
    border-radius: 50%;
    background: $brand-primary;
    color: $surface-white;

    svg {
      width: 1rem;
      height: 1rem;
    }
  }

  .info-title {
    margin: 0 0 0.3rem;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $text-primary;
  }

  ul {
    margin: 0;
    padding-left: 1.1rem;
    display: flex;
    flex-direction: column;
    gap: 0.2rem;
  }

  li {
    font-size: $font-size-sm;
    color: $text-secondary;
  }
}
</style>
