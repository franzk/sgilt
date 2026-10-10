<template>
  <aside class="event-sidebar">
    <!-- ── Carte de l'événement ─────────────────────────────────────────────────── -->
    <div class="event-card" :style="{ backgroundImage: `url(${coverImage})` }">
      <div class="overlay" />
      <div class="card-content">
        <h2 class="title">{{ event.title }}</h2>
        <span v-if="event.date" class="info-line">
          <CalendarEventIcon class="icon" />{{ formatDate(event.date) }}
        </span>
        <span v-if="place" class="info-line"><MapPin2Icon class="icon" />{{ place }}</span>
        <div class="card-footer">
          <span v-if="event.nbInvites" class="info-line">
            <GroupIcon class="icon" />{{ event.nbInvites }}
          </span>
          <NuxtLink :to="`${boardPath}/parametres`" class="edit">{{ $t('common.edit') }}</NuxtLink>
        </div>
      </div>
    </div>

    <!-- ── Navigation ───────────────────────────────────────────────────────────── -->
    <nav class="nav">
      <NuxtLink :to="boardPath" class="item" :class="{ active: activeKey === HOME_KEY }">
        <Home2Icon class="item-icon" />
        <span class="label">{{ $t('nav.home') }}</span>
      </NuxtLink>
      <NuxtLink
        :to="`${boardPath}/reservations`"
        class="item"
        :class="{ active: activeKey === PRESTATAIRES_KEY }"
      >
        <TeamIcon class="item-icon" />
        <span class="label">{{ $t('nav.prestataires') }}</span>
      </NuxtLink>

      <h3 class="section-title">{{ $t('evenement.rubriques-title') }}</h3>
      <!-- Même élément que la liste des rubriques du board mobile. -->
      <EventRubriqueItem
        v-for="rubrique in rubriques"
        :key="rubrique.key"
        :rubrique-key="rubrique.key"
        :count="rubrique.reservations.length"
        :active="activeKey === rubrique.key"
        @click="navigateTo(`${boardPath}/${rubrique.key}`)"
      />
    </nav>
  </aside>
</template>

<script setup lang="ts">
// Sidebar desktop d'un événement : carte de l'événement (« Modifier » → paramètres), accueil,
// prestataires et rubriques. Affichée par la route parente /app/events/:eventId.
import {
  CalendarEventIcon,
  GroupIcon,
  Home2Icon,
  MapPin2Icon,
  TeamIcon,
} from '@remixicons/vue/line'
import EventRubriqueItem from '~/components/evenement/EventRubriqueItem.vue'
import type { EventMeta } from '~/data/evenement/domain/EventMeta'
import type { EventRubrique } from '~/data/evenement/domain/EventRubrique'
import { formatDate } from '~/utils/dateUtils'

const HOME_KEY = 'accueil'
const PRESTATAIRES_KEY = 'prestataires'

const props = defineProps<{
  eventId: string
  event: EventMeta
  // Rubriques dans l'ordre d'affichage.
  rubriques: EventRubrique[]
  coverImage: string
}>()

const route = useRoute()

const boardPath = computed(() => `/app/events/${props.eventId}`)
const place = computed(() => [props.event.lieu, props.event.ville].filter(Boolean).join(', '))

// Entrée en surbrillance, d'après le premier segment de l'URL :
// Prestataires sur la liste des réservations (/reservations, /reservations/:id),
// la rubrique ouverte (/:rubrique, /:rubrique/:id),
// Accueil sinon.
const activeKey = computed(() => {
  const [section] = route.path.slice(boardPath.value.length + 1).split('/')
  if (section === 'reservations') return PRESTATAIRES_KEY
  if (props.rubriques.some((rubrique) => rubrique.key === section)) return section
  return HOME_KEY
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.event-sidebar {
  position: sticky;
  top: $app-header-height;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  width: 18rem;
  height: calc(100dvh - #{$app-header-height});
  padding: $spacing-m;
  overflow-y: auto;
  border-right: 1px solid $divider-color;
  background: $surface-white;

  // ── Carte de l'événement ─────────────────────────────────────────────────────
  .event-card {
    position: relative;
    flex-shrink: 0;
    border-radius: $radius-lg;
    overflow: hidden;
    background-size: cover;
    background-position: center;

    .overlay {
      position: absolute;
      inset: 0;
      background: linear-gradient(to bottom, rgba(47, 42, 37, 0.15), rgba(47, 42, 37, 0.75));
    }

    .card-content {
      position: relative;
      display: flex;
      flex-direction: column;
      gap: 0.375rem;
      padding: $spacing-l $spacing-m $spacing-m;
      color: #fff;

      .title {
        margin: 0 0 $spacing-xxs;
        font-family: 'Cormorant Garamond', serif;
        font-size: 1.6rem;
        font-weight: 600;
        line-height: 1.1;
        text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
      }

      .info-line {
        display: inline-flex;
        align-items: center;
        gap: 0.375rem;
        font-size: 0.8rem;
        font-weight: 500;

        .icon {
          flex-shrink: 0;
          width: 0.9rem;
          height: 0.9rem;
        }
      }

      .card-footer {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: $spacing-s;

        .edit {
          margin-left: auto;
          padding: $spacing-xxs $spacing-s;
          border-radius: 9999px;
          background: $surface-white;
          color: $text-primary;
          font-size: 0.8rem;
          font-weight: 600;
          text-decoration: none;
        }
      }
    }
  }

  // ── Navigation ───────────────────────────────────────────────────────────────
  .nav {
    display: flex;
    flex-direction: column;
    gap: $spacing-xxs;

    .item {
      display: flex;
      align-items: center;
      gap: $spacing-s;
      padding: $spacing-s;
      border-radius: $radius-md;
      color: $text-primary;
      text-decoration: none;
      transition: background 120ms ease;

      &:hover {
        background: $surface-soft;
      }

      &.active {
        background: rgba($brand-accent, 0.15);
      }

      .item-icon {
        flex-shrink: 0;
        width: 1.375rem;
        height: 1.375rem;
      }

      .label {
        font-size: 0.95rem;
        font-weight: 600;
      }
    }

    .section-title {
      margin: $spacing-m 0 $spacing-xxs;
      padding: 0 $spacing-s;
      font-size: 0.7rem;
      font-weight: 700;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: $text-secondary;
    }
  }
}
</style>
