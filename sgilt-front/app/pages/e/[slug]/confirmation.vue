<template>
  <div class="confirmation-page">
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
        <span class="value">{{ quantity }}</span>
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

    <div class="email-notice">
      <TicketIcon class="icon" />
      <div>
        <p class="notice-title">{{ $t('ticketing.confirmation.email-notice-title') }}</p>
        <p class="notice-sub">{{ $t('ticketing.order.email-notice-sub') }}</p>
      </div>
    </div>

    <button type="button" class="primary-cta" @click="onViewTickets">
      <TicketIcon class="icon" />
      {{ $t('ticketing.confirmation.view-tickets') }}
      <ArrowRightSIcon class="chevron" aria-hidden="true" />
    </button>

    <button type="button" class="secondary-cta" @click="onAddToCalendar">
      <CalendarEventIcon class="icon" />
      {{ $t('ticketing.confirmation.add-to-calendar') }}
      <ArrowRightSIcon class="chevron" aria-hidden="true" />
    </button>

    <div class="practical-info">
      <span class="info-badge" aria-hidden="true"><InformationIcon /></span>
      <div>
        <p class="info-title">{{ $t('ticketing.confirmation.practical-info-title') }}</p>
        <ul>
          <li v-for="line in practicalInfo" :key="line">{{ line }}</li>
        </ul>
      </div>
    </div>

    <NuxtLink :to="`/e/${slug}`" class="secondary-cta back-link">
      <ArrowLeftSIcon class="icon" aria-hidden="true" />
      {{ $t('ticketing.confirmation.back-to-event') }}
    </NuxtLink>
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
} from '@remixicons/vue/line'
import { mockEvent } from '~/data/ticketing/mockEvent'

const { t } = useI18n()
const route = useRoute()
const slug = route.params.slug as string

const event = mockEvent

useHead({ title: `${t('ticketing.confirmation.title')} · ${event.title} · Sgilt` })

// Repris depuis la page paiement (query `qty`) — pas de commande persistée côté back pour l'instant.
const MIN_QUANTITY = 1

const initialQty = Number(route.query.qty)
const quantity =
  Number.isInteger(initialQty) && initialQty >= MIN_QUANTITY && initialQty <= event.remainingTickets
    ? initialQty
    : MIN_QUANTITY

const unitPriceLabel = computed(() => `${event.unitPrice} €`)
const totalLabel = computed(() => `${event.unitPrice * quantity} €`)

/**
 * Copie spécifique à cet écran (distincte de `event.importantInfo` affiché avant l'achat) — pas
 * encore de modèle back pour ce contenu post-confirmation.
 */
const practicalInfo = [
  'Événement en plein air, places debout.',
  "Aucun billet physique. Présentez vos billets depuis votre smartphone à l'entrée.",
  "En cas d'empêchement, contactez l'organisateur.",
]

// Pas de vraie billetterie/wallet ni de génération de fichier .ics pour l'instant.
function onViewTickets(): void {
  console.log(`Voir mes billets pour "${event.title}" (slug=${slug})`)
}

function onAddToCalendar(): void {
  console.log(`Ajouter au calendrier "${event.title}" (slug=${slug})`)
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

// ─── Bandeau e-mail ──────────────────────────────────────────────────────────
.email-notice {
  display: flex;
  align-items: flex-start;
  gap: $spacing-s;
  padding: $spacing-m;
  background: rgba($state-success, 0.08);
  border-radius: $radius-md;
  color: $state-success;

  .icon {
    flex-shrink: 0;
    width: 1.25rem;
    height: 1.25rem;
    margin-top: 0.1rem;
  }

  .notice-title {
    margin: 0;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $text-primary;
  }

  .notice-sub {
    margin: 0;
    font-size: $font-size-xs;
    color: $text-secondary;
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
