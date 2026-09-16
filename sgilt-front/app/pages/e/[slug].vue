<template>
  <div class="ticket-event-page">
    <SgiltHero
      :medias="eventMedias"
      :tag="event.tag"
      :title="event.title"
      show-share
      :share-text="shareText"
    >
      <template #subtitle>
        <p>{{ event.subtitle }}</p>
      </template>
    </SgiltHero>

    <!-- Mobile : carte qui empiète sur le bas de la photo — date + lieu uniquement. -->
    <div class="info-card">
      <div class="info-card-item">
        <span class="badge"><CalendarEventIcon /></span>
        <div class="text">
          <p class="main">{{ event.dateLabel }}</p>
          <p class="sub">{{ event.timeLabel }}</p>
        </div>
      </div>
      <div class="info-card-item">
        <span class="badge"><MapPin2Icon /></span>
        <p class="venue">{{ event.venue }} · {{ event.city }}</p>
        <ArrowRightSIcon class="chevron" aria-hidden="true" />
      </div>
    </div>

    <!-- Desktop : ligne classique sous la photo — date et lieu alignés. -->
    <div class="info-row">
      <div class="info-item">
        <CalendarEventIcon class="icon" />
        <div class="text">
          <p class="main">{{ event.dateLabel }}</p>
          <p class="sub">{{ event.timeLabel }}</p>
        </div>
      </div>
      <div class="info-item">
        <MapPin2Icon class="icon" />
        <div class="text">
          <p class="main">{{ event.venue }}</p>
          <p class="sub">{{ event.city }}</p>
        </div>
      </div>
    </div>

    <div class="layout">
      <div class="content-col">
        <h2 class="section-title">{{ event.sectionTitle }}</h2>

        <section class="description">
          <p>{{ event.description }}</p>
        </section>

        <section class="pictos">
          <div v-for="picto in PICTOS" :key="picto.label" class="picto">
            <component :is="picto.icon" class="icon" />
            <p class="label">{{ picto.label }}</p>
            <p class="sublabel">{{ picto.sublabel }}</p>
          </div>
        </section>
      </div>

      <aside class="sidebar">
        <section class="pricing">
          <div class="price-row">
            <div class="price">
              <span class="amount">{{ unitPriceLabel }}</span>
              <span class="unit">{{ $t('ticketing.event.per-ticket') }}</span>
            </div>
            <span class="availability-badge" :class="{ unavailable: !event.available }">
              <span class="dot" aria-hidden="true" />
              {{
                event.available
                  ? $t('ticketing.event.available')
                  : $t('ticketing.event.unavailable')
              }}
            </span>
          </div>

          <div class="quantity-row">
            <span class="label">{{ $t('ticketing.event.quantity-label') }}</span>
            <div class="stepper">
              <button
                type="button"
                class="step-btn"
                :aria-label="$t('ticketing.event.decrement-aria')"
                @click="decrement"
              >
                <SubtractIcon />
              </button>
              <span class="value">{{ quantity }}</span>
              <button
                type="button"
                class="step-btn"
                :aria-label="$t('ticketing.event.increment-aria')"
                @click="increment"
              >
                <AddIcon />
              </button>
            </div>
          </div>

          <div class="total-row">
            <span class="label">{{ $t('ticketing.event.total-label') }}</span>
            <span class="value">{{ totalLabel }}</span>
          </div>

          <button type="button" class="cta" @click="onBuyClick">
            <TicketIcon class="icon" />
            {{ $t('ticketing.event.cta') }}
            <ArrowRightSIcon class="chevron" aria-hidden="true" />
          </button>

          <Transition name="fade">
            <p v-if="purchaseError" class="purchase-error">{{ purchaseError }}</p>
          </Transition>

          <p class="reassurance">
            <LockIcon class="icon" aria-hidden="true" />
            {{ $t('ticketing.event.reassurance') }} <span class="brand">Sgilt</span>
          </p>
        </section>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { markRaw, type Component } from 'vue'
import SgiltHero, { type HeroMedia } from '~/components/basics/media/SgiltHero.vue'
import {
  CalendarEventIcon,
  MapPin2Icon,
  MusicIcon,
  GobletIcon,
  LeafIcon,
  TeamIcon,
  TicketIcon,
  ArrowRightSIcon,
  LockIcon,
  AddIcon,
  SubtractIcon,
} from '@remixicons/vue/line'

const { t } = useI18n()
const route = useRoute()
const slug = route.params.slug as string

/**
 * Données de démonstration en dur pour ce brief — le vrai contenu (titre, date, lieu, tarif,
 * pictogrammes) viendra du modèle Outil/Billetterie, pas encore construit. Le slug de route
 * n'est pas encore résolu contre un événement réel.
 */
const event = {
  tag: 'Édition du 12 mars',
  title: 'Les Jeudis du Taennel',
  subtitle: "Concert & bar éphémère au cœur du vignoble",
  // Clé R2 (sgilt-r2-mock/storage/bank/taennel.png) — comme un vrai media prestataire/événement,
  // pas un asset public Nuxt.
  heroImage: 'bank/taennel.png',
  dateLabel: 'Jeudi 12 mars 2026',
  timeLabel: 'À partir de 19h30',
  venue: 'Le Taennel',
  city: 'Scherwiller (67)',
  sectionTitle: 'Une soirée musicale et conviviale',
  description:
    "Le jeudi, le Taennel se transforme : concert acoustique, bar à vin nature et food-truck sur place. Une soirée conviviale pour démarrer le week-end en avance, en plein cœur du vignoble.",
  unitPrice: 12,
  available: true,
}

const eventMedias: HeroMedia[] = [{ type: 'IMAGE', ref: event.heroImage, position: 0 }]

useHead({ title: `${event.title} · Sgilt` })

const { toUrl } = useImageUrl()
useSeoMeta({
  ogTitle: `${event.title} · Sgilt`,
  ogImage: toUrl(event.heroImage),
})

const shareText = computed(() => t('ticketing.event.share-text', { name: event.title }))

interface EventPicto {
  icon: Component
  label: string
  sublabel: string
}

const PICTOS: EventPicto[] = [
  { icon: markRaw(MusicIcon), label: 'Concert live', sublabel: 'À partir de 20h' },
  { icon: markRaw(GobletIcon), label: 'Vins & restauration', sublabel: 'Sur place' },
  { icon: markRaw(LeafIcon), label: 'Cadre unique', sublabel: 'Au cœur du vignoble' },
  { icon: markRaw(TeamIcon), label: 'Ambiance conviviale', sublabel: 'Locale et authentique' },
]

// ── Sélecteur de quantité ─────────────────────────────────────────────────────
const MIN_QUANTITY = 1
const MAX_QUANTITY = 10

const quantity = ref(MIN_QUANTITY)

function decrement(): void {
  quantity.value = Math.max(MIN_QUANTITY, quantity.value - 1)
}

function increment(): void {
  quantity.value = Math.min(MAX_QUANTITY, quantity.value + 1)
}

const unitPriceLabel = computed(() => `${event.unitPrice} €`)
const totalLabel = computed(() => `${event.unitPrice * quantity.value} €`)

// ── Achat ─────────────────────────────────────────────────────────────────────
// Le bouton reste toujours cliquable (jamais `disabled`) : une indisponibilité se traduit par
// une erreur explicite au clic plutôt qu'un blocage silencieux.
const purchaseError = ref<string | null>(null)

function onBuyClick(): void {
  if (!event.available) {
    purchaseError.value = t('ticketing.event.sold-out-error')
    return
  }
  purchaseError.value = null
  console.log(`Achat de ${quantity.value} billet(s) pour l'événement "${event.title}" (slug=${slug})`)
  // navigateTo(`/e/${slug}/commande`)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Colonne de contenu centrée, utilisée pour aligner le texte du hero et la mise en page
// deux-colonnes desktop sur le même repère horizontal (photo du hero en plein bord, elle).
%container-x {
  width: 100%;
  max-width: $container-max-width;
  margin: 0 auto;
  padding: 0 $spacing-m;

  @media (min-width: $breakpoint-desktop) {
    padding: 0 $section-padding-x;
  }
}

.ticket-event-page {
  display: flex;
  flex-direction: column;
  width: 100%;
}

// Le hero (photo + dégradé + tag/titre/sous-titre) vient entièrement de SgiltHero, partagé avec
// PrestataireHero.vue — même taille (aspect-ratio 3/2 mobile, 55vh desktop), même typographie.

// ─── Carte infos mobile (empiète sur le bas du hero) ─────────────────────────────
.info-card {
  position: relative;
  z-index: 1;
  margin: -1rem $spacing-m 0;
  padding: 0 $spacing-m;
  background: $surface-white;
  border-radius: $radius-lg;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);

  @media (min-width: $breakpoint-desktop) {
    display: none;
  }

  .info-card-item {
    display: flex;
    align-items: center;
    gap: $spacing-s;
    padding: $spacing-m 0;

    &:not(:last-child) {
      border-bottom: 1px solid $divider-color;
    }
  }

  .badge {
    flex-shrink: 0;
    width: 2.5rem;
    height: 2.5rem;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba($state-success, 0.12);
    color: $state-success;

    svg {
      width: 1.25rem;
      height: 1.25rem;
    }
  }

  .text {
    display: flex;
    flex-direction: column;
    min-width: 0;
  }

  .main {
    margin: 0;
    font-size: $font-size-md;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .sub {
    margin: 0;
    font-size: $font-size-sm;
    color: $text-secondary;
  }

  .venue {
    flex: 1;
    min-width: 0;
    margin: 0;
    font-size: $font-size-md;
    font-weight: $font-weight-medium;
    color: $text-primary;
  }

  .chevron {
    flex-shrink: 0;
    width: 1.25rem;
    height: 1.25rem;
    color: $text-secondary;
  }
}

// ─── Ligne infos desktop (date / lieu / condition d'accès) ───────────────────────
.info-row {
  @extend %container-x;
  display: none;
  gap: $spacing-xl;
  padding-top: $spacing-l;
  padding-bottom: $spacing-l;
  border-bottom: 1px solid $divider-color;

  @media (min-width: $breakpoint-desktop) {
    display: flex;
    flex-direction: row;
  }

  .info-item {
    display: flex;
    align-items: center;
    gap: $spacing-s;
  }

  .icon {
    flex-shrink: 0;
    width: 1.25rem;
    height: 1.25rem;
    color: $text-secondary;
  }

  .text {
    display: flex;
    flex-direction: column;
  }

  .main {
    margin: 0;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $text-primary;
  }

  .sub {
    margin: 0;
    font-size: $font-size-xs;
    color: $text-secondary;
  }
}

// ─── Mise en page desktop : contenu + carte tarif en colonne ────────────────────
.layout {
  @extend %container-x;
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding-top: $spacing-l;
  padding-bottom: $spacing-xxl;

  @media (min-width: $breakpoint-desktop) {
    display: grid;
    grid-template-columns: 1fr 380px;
    align-items: start;
    gap: $spacing-xxl;
    padding-top: $spacing-xxl;
  }
}

.content-col {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
}

// ─── Titre de section (desktop uniquement, absent de la maquette mobile) ────────
.section-title {
  display: none;

  @media (min-width: $breakpoint-desktop) {
    display: block;
    font-family: 'Cormorant Garamond', serif;
    font-size: $font-size-2xl;
    font-weight: 700;
    color: $text-primary;
    margin: 0;
  }
}

// ─── Description ────────────────────────────────────────────────────────────────
.description {
  p {
    margin: 0;
    font-size: $font-size-md;
    line-height: $line-height-relaxed;
    color: $text-primary;
  }
}

// ─── Pictogrammes ───────────────────────────────────────────────────────────────
.pictos {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-l $spacing-m;
  padding-bottom: $spacing-l;
  border-bottom: 1px solid $divider-color;

  @media (min-width: $breakpoint-desktop) {
    grid-template-columns: repeat(4, 1fr);
    border-bottom: none;
    padding-bottom: 0;
  }

  .picto {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 0.3rem;
  }

  .icon {
    width: 1.5rem;
    height: 1.5rem;
    // Palette pictogrammes : vert, distinct de l'accent doré réservé au CTA/marque.
    // Cf. brief — à confirmer/ajuster une fois intégré en conditions réelles.
    color: $state-success;
  }

  .label {
    margin: 0;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $text-primary;
  }

  .sublabel {
    margin: 0;
    font-size: $font-size-xs;
    color: $text-secondary;
  }
}

// ─── Carte tarif (sidebar desktop) ────────────────────────────────────────────
.sidebar {
  @media (min-width: $breakpoint-desktop) {
    margin-top: -$spacing-xl;
    background: $surface-white;
    border-radius: $radius-lg;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
    padding: $spacing-l;
  }
}

.pricing {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-s;
}

.price {
  display: flex;
  align-items: baseline;
  gap: 0.4rem;

  .amount {
    font-size: $font-size-2xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .unit {
    font-size: $font-size-sm;
    color: $text-secondary;
  }
}

.availability-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.35rem 0.8rem;
  border-radius: 2rem;
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  white-space: nowrap;
  background: rgba($state-available, 0.1);
  color: $state-available;

  .dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: currentColor;
    flex-shrink: 0;
  }

  &.unavailable {
    background: rgba($state-danger, 0.1);
    color: $state-danger;
  }
}

.quantity-row,
.total-row {
  display: flex;
  align-items: center;
  justify-content: space-between;

  .label {
    font-size: $font-size-md;
    font-weight: $font-weight-medium;
    color: $text-primary;
  }
}

.total-row {
  padding-top: $spacing-m;
  border-top: 1px solid $divider-color;

  .value {
    font-size: $font-size-xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }
}

.stepper {
  display: flex;
  align-items: center;
  gap: $spacing-m;
}

.step-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.25rem;
  height: 2.25rem;
  border: none;
  border-radius: 50%;
  background: $surface-soft;
  color: $text-primary;
  cursor: pointer;
  transition: background 150ms ease;

  &:hover {
    background: $brand-subtle;
  }

  svg {
    width: 1rem;
    height: 1rem;
  }
}

.stepper .value {
  min-width: 1.5rem;
  text-align: center;
  font-size: $font-size-lg;
  font-weight: $font-weight-semibold;
  color: $text-primary;
}

// ─── CTA ────────────────────────────────────────────────────────────────────────
.cta {
  width: 100%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5em;
  height: 3.25rem;
  border: none;
  border-radius: 9999px;
  background: $brand-accent;
  color: $brand-primary;
  font-family: inherit;
  font-size: $font-size-md;
  font-weight: $font-weight-bold;
  cursor: pointer;
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

  .icon {
    width: 1.1rem;
    height: 1.1rem;
  }

  .chevron {
    width: 1.1rem;
    height: 1.1rem;
  }
}

.purchase-error {
  margin: -$spacing-xs 0 0;
  font-size: $font-size-sm;
  color: $state-error;
  text-align: center;
}

.reassurance {
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-xs;
  color: $text-secondary;
  font-size: $font-size-xs;

  .icon {
    width: 0.9rem;
    height: 0.9rem;
    flex-shrink: 0;
  }

  .brand {
    text-decoration: underline;
    font-weight: $font-weight-medium;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 200ms ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
