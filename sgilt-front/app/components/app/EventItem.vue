<!--
  Carte d'un événement dans la liste « Mes événements » de l'accueil client (/app).
  Deux formats : big (prochain événement, photo plein cadre, badge et résumé des réservations)
  et small (vignette + date/lieu, badge optionnel ; sur desktop, date et lieu sur une ligne et
  badge à droite). Le clic remonte via l'événement click.
-->
<template>
  <!-- ── Big ─────────────────────────────────────────────────────────────────── -->
  <SgiltCard
    v-if="format === 'big'"
    format="big"
    tag="button"
    class="big-card"
    :image="coverUrl"
    :ratio="ratio"
    @click="$emit('click')"
  >
    <template #overlay>
      <span v-if="badge" class="badge">
        <CalendarEventIcon class="badge-icon" aria-hidden="true" />
        {{ badge }}
      </span>
      <div class="info">
        <h2 class="title">{{ title }}</h2>
        <p v-if="date" class="line">
          <CalendarEventIcon class="line-icon" aria-hidden="true" />
          {{ formatDate(date) }}
        </p>
        <p v-if="ville" class="line">
          <MapPin2Icon class="line-icon" aria-hidden="true" />
          {{ ville }}
        </p>
        <div class="bottom">
          <span v-if="summary" class="summary">
            <CheckboxCircleIcon class="summary-icon" aria-hidden="true" />
            {{ summary }}
          </span>
          <span class="chevron" aria-hidden="true">
            <ArrowRightSIcon class="chevron-icon" />
          </span>
        </div>
      </div>
    </template>
  </SgiltCard>

  <!-- ── Small ────────────────────────────────────────────────────────────────── -->
  <SgiltCard v-else format="small" tag="button" class="small-card" @click="$emit('click')">
    <template #avatar>
      <img class="cover" :src="coverUrl" :alt="title" />
    </template>

    <div class="body">
      <p class="title">{{ title }}</p>
      <div class="lines">
        <p v-if="date" class="line">
          <CalendarEventIcon class="line-icon" aria-hidden="true" />
          {{ formatDate(date) }}
        </p>
        <p v-if="ville" class="line">
          <MapPin2Icon class="line-icon" aria-hidden="true" />
          {{ ville }}
        </p>
      </div>
      <span v-if="badge" class="small-badge">{{ badge }}</span>
    </div>

    <template #cta>
      <ArrowRightSIcon class="arrow" aria-hidden="true" />
    </template>
  </SgiltCard>
</template>

<script setup lang="ts">
import {
  ArrowRightSIcon,
  CalendarEventIcon,
  CheckboxCircleIcon,
  MapPin2Icon,
} from '@remixicons/vue/line'
import SgiltCard from '~/components/basics/cards/SgiltCard.vue'
import { RESERVATION_STATUS_CONFIG } from '~/constants/reservation-status'
import { BANK_IMAGE_PATHS } from '~/utils/eventCovers'

const props = withDefaults(
  defineProps<{
    title: string
    date?: Date
    ville?: string
    coverImage?: string | null
    eventType?: string
    format?: 'big' | 'small'
    summary?: string
    badge?: string
    ratio?: string
  }>(),
  {
    format: 'small',
    ratio: '3/2',
  },
)

defineEmits<{ click: [] }>()

// Badge des petites cartes : il compte les échanges en cours, on reprend la couleur du statut
const SMALL_BADGE_COLORS = RESERVATION_STATUS_CONFIG.en_discussion

const { toUrl } = useImageUrl()

const coverUrl = computed(() => {
  const path =
    props.coverImage ?? BANK_IMAGE_PATHS[props.eventType ?? ''] ?? BANK_IMAGE_PATHS.autre!
  return toUrl(path)
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ── Big ──────────────────────────────────────────────────────────────────────────
.big-card {
  .badge {
    position: absolute;
    top: $spacing-s;
    left: $spacing-s;
    display: inline-flex;
    align-items: center;
    gap: 0.375rem;
    padding: 0.375rem 0.75rem;
    border-radius: 999px;
    background: rgba(255, 248, 225, 0.95);
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    font-weight: 600;
    color: $text-primary;

    .badge-icon {
      width: 1rem;
      height: 1rem;
    }
  }

  .info {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
    text-align: left;

    .title {
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.6rem;
      font-weight: 600;
      color: #fff;
      margin: 0 0 0.25rem;
      line-height: 1.15;
      text-shadow: 0 1px 4px rgba(0, 0, 0, 0.25);
    }

    .line {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-family: 'Inter', sans-serif;
      font-size: 0.875rem;
      color: rgba(255, 255, 255, 0.9);
      margin: 0;

      .line-icon {
        width: 1rem;
        height: 1rem;
        flex-shrink: 0;
      }
    }

    .bottom {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: $spacing-s;
      margin-top: $spacing-s;

      .summary {
        display: inline-flex;
        align-items: center;
        gap: 0.375rem;
        padding: 0.375rem 0.75rem;
        border-radius: 999px;
        background: rgba(255, 255, 255, 0.18);
        backdrop-filter: blur(4px);
        font-family: 'Inter', sans-serif;
        font-size: 0.75rem;
        font-weight: 500;
        color: #fff;

        .summary-icon {
          width: 1rem;
          height: 1rem;
          color: $brand-accent;
        }
      }

      .chevron {
        margin-left: auto;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 2rem;
        height: 2rem;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.18);
        backdrop-filter: blur(4px);
        color: #fff;

        .chevron-icon {
          width: 1.25rem;
          height: 1.25rem;
        }
      }
    }
  }
}

// ── Small ─────────────────────────────────────────────────────────────────────────
.small-card {
  :deep(.avatar) {
    width: 6rem;
    height: 4.5rem;
    border-radius: $radius-md;

    .cover {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .body {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    min-width: 0;

    @media (min-width: $breakpoint-desktop) {
      display: grid;
      grid-template-columns: minmax(0, 1fr) auto;
      align-items: center;
      column-gap: $spacing-m;
    }

    .title {
      max-width: 100%;
      font-family: 'Inter', sans-serif;
      font-size: 0.95rem;
      font-weight: 600;
      color: $text-primary;
      margin: 0 0 0.25rem;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;

      @media (min-width: $breakpoint-desktop) {
        grid-column: 1;
        font-size: 1.05rem;
      }
    }

    .lines {
      max-width: 100%;

      @media (min-width: $breakpoint-desktop) {
        grid-column: 1;
        display: flex;
        gap: $spacing-l;
      }

      .line {
        display: flex;
        align-items: center;
        gap: 0.375rem;
        font-family: 'Inter', sans-serif;
        font-size: 0.8rem;
        color: $text-secondary;
        margin: 0.125rem 0 0;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;

        @media (min-width: $breakpoint-desktop) {
          font-size: 0.875rem;
        }

        .line-icon {
          width: 0.9rem;
          height: 0.9rem;
          flex-shrink: 0;
        }
      }
    }

    .small-badge {
      display: inline-block;
      margin-top: 0.375rem;
      padding: 0.125rem 0.5rem;
      border-radius: $radius-sm;
      background: v-bind('SMALL_BADGE_COLORS.bgColor');
      font-family: 'Inter', sans-serif;
      font-size: 0.8rem;
      font-weight: 700;
      color: v-bind('SMALL_BADGE_COLORS.color');

      @media (min-width: $breakpoint-desktop) {
        grid-column: 2;
        grid-row: 1 / span 2;
        margin-top: 0;
        padding: 0.25rem 0.75rem;
        border-radius: 999px;
        font-size: 0.85rem;
      }
    }
  }

  .arrow {
    width: 1.25rem;
    height: 1.25rem;
    color: $text-secondary;
  }
}
</style>
