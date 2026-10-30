<!--
  Fiche publique d'un événement (/e/[slug]) : présentation de l'événement et, s'il a une
  billetterie, son bloc d'achat (TicketingPurchaseCard) selon l'état de celle-ci. Événement chargé
  par usePublicEvent (slug inconnu : 404).
-->
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
          <p class="main">{{ dateLabel }}</p>
          <p v-if="timeLabel" class="sub">{{ timeLabel }}</p>
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
          <p class="main">{{ dateLabel }}</p>
          <p v-if="timeLabel" class="sub">{{ timeLabel }}</p>
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

    <div class="layout" :class="{ 'with-ticketing': !!event.ticketing }">
      <div class="content-col">
        <h2 v-if="event.sectionTitle" class="section-title">{{ event.sectionTitle }}</h2>

        <section class="description">
          <p>{{ event.description }}</p>
        </section>

        <section class="pictos">
          <div v-for="picto in event.pictos" :key="picto.label" class="picto">
            <component :is="PICTO_ICONS[picto.icon]" class="icon" />
            <p class="label">{{ picto.label }}</p>
            <p class="sublabel">{{ picto.sublabel }}</p>
          </div>
        </section>
      </div>

      <aside v-if="event.ticketing" class="sidebar">
        <TicketingPurchaseCard :slug="slug" :ticketing="event.ticketing" />
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { markRaw, type Component } from 'vue'
import SgiltHero, { type HeroMedia } from '~/components/basics/media/SgiltHero.vue'
import TicketingPurchaseCard from '~/components/ticketing/TicketingPurchaseCard.vue'
import {
  CalendarEventIcon,
  MapPin2Icon,
  MusicIcon,
  GobletIcon,
  LeafIcon,
  TeamIcon,
  ArrowRightSIcon,
} from '@remixicons/vue/line'
import type { PublicEventPictoIcon } from '~/data/ticketing/domain/PublicEvent'
import { usePublicEvent } from '~/composables/usePublicEvent'
import { useEventHoursLabel } from '~/composables/useEventHoursLabel'
import { formatDateWithWeekday } from '~/utils/dateUtils'

const { t } = useI18n()
const route = useRoute()
const slug = String(route.params.slug)

const event = await usePublicEvent(slug)
const { eventHoursLabel } = useEventHoursLabel()
const dateLabel = formatDateWithWeekday(event.date)
const timeLabel = eventHoursLabel(event.startsAt, event.endsAt)

const eventMedias: HeroMedia[] = [{ type: 'IMAGE', ref: event.heroImage, position: 0 }]

useHead({ title: event.title })

const { toUrl } = useImageUrl()
useSeoMeta({
  ogTitle: `${event.title} · Sgilt`,
  ogImage: toUrl(event.heroImage),
})

const shareText = computed(() => t('ticketing.event.share-text', { name: event.title }))

const PICTO_ICONS: Record<PublicEventPictoIcon, Component> = {
  music: markRaw(MusicIcon),
  goblet: markRaw(GobletIcon),
  leaf: markRaw(LeafIcon),
  team: markRaw(TeamIcon),
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
    grid-template-columns: 1fr;
    align-items: start;
    gap: $spacing-xxl;
    padding-top: $spacing-xxl;

    // Colonne de la carte tarif seulement si l'événement a une billetterie.
    &.with-ticketing {
      grid-template-columns: 1fr 380px;
    }
  }
}

.content-col {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
}

// ─── Titre de section (facultatif) ──────────────────────────────────────────────
.section-title {
  margin: 0;
  font-family: 'Cormorant Garamond', serif;
  font-size: $font-size-xl;
  font-weight: 700;
  color: $text-primary;

  @media (min-width: $breakpoint-desktop) {
    font-size: $font-size-2xl;
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
</style>
