<template>
  <div class="evenement-home">
    <!-- ── Accroche ─────────────────────────────────────────────────────────────── -->
    <section class="hero">
      <div class="hero-image" :style="{ backgroundImage: `url(${coverImage})` }" />
      <div class="hero-text">
        <h1 class="title">{{ $t(`evenement.home.hero.title.${typeKey}`) }}</h1>
        <p class="intro">{{ $t('evenement.home.hero.intro') }}</p>
      </div>
    </section>

    <!-- ── Votre événement en un coup d'œil ─────────────────────────────────────── -->
    <section class="glance">
      <h2 class="glance-title">{{ $t('evenement.home.glance.title') }}</h2>
      <div class="glance-row">
        <div v-for="info in infos" :key="info.key" class="info">
          <component :is="info.icon" class="icon" aria-hidden="true" />
          <div class="info-text">
            <span class="label">{{ $t(`evenement.home.glance.${info.key}`) }}</span>
            <span class="value">{{ info.value || $t('evenement.home.glance.to-define') }}</span>
          </div>
        </div>
        <NuxtLink to="/evenement/parametres" class="edit">
          <PencilIcon class="icon" aria-hidden="true" />
          {{ $t('evenement.home.glance.edit') }}
        </NuxtLink>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
// Accueil desktop de l'événement local (parcours public, /evenement) : accroche, l'événement en un
// coup d'œil. La carte et les rubriques sont dans la sidebar.
import {
  CalendarEventIcon,
  GroupIcon,
  Heart3Icon,
  MapPin2Icon,
  PencilIcon,
} from '@remixicons/vue/line'
import { eventTypeKey } from '~/types/evenement'
import { formatDate } from '~/utils/dateUtils'

defineProps<{
  coverImage: string
}>()

const { localEvent, eventTypeLabel } = useLocalEvent()
const { nbInvitesLabel } = useNbInvitesLabel()

const typeKey = computed(() => eventTypeKey(localEvent.eventType) ?? 'autre')

// Valeur vide : « À définir ».
const infos = computed(() => [
  { key: 'date', icon: CalendarEventIcon, value: formatDate(localEvent.date) },
  { key: 'nb-invites', icon: GroupIcon, value: nbInvitesLabel(localEvent.nbInvites) },
  { key: 'type', icon: Heart3Icon, value: eventTypeLabel.value },
  {
    key: 'lieu',
    icon: MapPin2Icon,
    value: [localEvent.lieu, localEvent.ville].filter(Boolean).join(', '),
  },
])
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.evenement-home {
  display: flex;
  flex-direction: column;
  gap: $spacing-xl;
  padding: 0 $spacing-xl $spacing-xl;

  // ── Accroche ─────────────────────────────────────────────────────────────────
  $hero-text-width: 40rem;

  // La couverture n'apparaît qu'après le texte, en fondu depuis le fond de page : le texte ne passe
  // jamais sur l'image (une couverture sombre le rendrait illisible).
  .hero {
    position: relative;
    margin: 0 (-$spacing-xl);
    padding: $spacing-xxl $spacing-xl $spacing-l;

    .hero-image {
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      background-size: cover;
      background-position: right center;
      mask-image: linear-gradient(
        to right,
        transparent calc(#{$spacing-xl} + #{$hero-text-width}),
        #000 calc(#{$spacing-xl} + #{$hero-text-width} + 12rem)
      );
    }

    .hero-text {
      position: relative;
      display: flex;
      flex-direction: column;
      gap: $spacing-s;
      max-width: $hero-text-width;

      .title {
        margin: 0;
        font-family: 'Cormorant Garamond', serif;
        font-size: $font-size-3xl;
        font-weight: $font-weight-semibold;
        color: $text-primary;
      }

      .intro {
        margin: 0;
        color: $text-secondary;
        font-size: $font-size-md;
        line-height: 1.5;
      }
    }
  }

  // ── Votre événement en un coup d'œil ─────────────────────────────────────────
  .glance {
    display: flex;
    flex-direction: column;
    gap: $spacing-m;
    padding: $spacing-l;
    border-radius: $radius-lg;
    background: $surface-soft;

    .glance-title {
      margin: 0;
      font-size: $font-size-lg;
      font-weight: $font-weight-bold;
      color: $text-primary;
    }

    .glance-row {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      gap: $spacing-m;

      .info {
        flex: 1;
        display: flex;
        align-items: flex-start;
        gap: $spacing-s;
        min-width: 10rem;
        padding-right: $spacing-m;
        border-right: 1px solid $divider-color;

        &:nth-last-child(2) {
          border-right: none;
        }

        .icon {
          flex-shrink: 0;
          width: 1.5rem;
          height: 1.5rem;
          color: $text-secondary;
        }

        .info-text {
          display: flex;
          flex-direction: column;
          gap: 0.125rem;

          .label {
            font-size: $font-size-sm;
            color: $text-secondary;
          }

          .value {
            font-size: $font-size-sm;
            font-weight: $font-weight-semibold;
            color: $text-primary;
          }
        }
      }

      .edit {
        display: inline-flex;
        align-items: center;
        gap: $spacing-xs;
        padding: $spacing-xs $spacing-m;
        border: 1px solid $divider-color;
        border-radius: 999px;
        background: $surface-white;
        color: $text-primary;
        font-size: $font-size-sm;
        font-weight: $font-weight-semibold;
        text-decoration: none;
        white-space: nowrap;

        .icon {
          width: 1rem;
          height: 1rem;
        }
      }
    }
  }
}
</style>
