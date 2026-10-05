<template>
  <div class="board">
    <!-- ── Couverture ─────────────────────────────────────────────────────────── -->
    <div ref="coverRef" class="cover" :style="{ backgroundImage: `url(${coverImage})` }">
      <div class="overlay" />
      <button
        class="settings-btn"
        type="button"
        :aria-label="$t('evenement.settings-aria')"
        @click="$emit('settings')"
      >
        <SettingsIcon class="settings-icon" />
      </button>
      <div class="cover-content">
        <h1 class="title">{{ eventMeta.title }}</h1>
        <div class="info-lines">
          <span v-if="eventMeta.date" class="info-line">
            <CalendarEventIcon class="icon" />{{ formatDate(eventMeta.date) }}
          </span>
          <span v-if="eventMeta.ville" class="info-line">
            <MapPin2Icon class="icon" />{{ eventMeta.ville }}
          </span>
          <span v-if="eventMeta.nbInvites" class="info-line">
            <GroupIcon class="icon" />{{ eventMeta.nbInvites }}
          </span>
        </div>
      </div>
    </div>

    <!-- ── Formule d'accroche ─────────────────────────────────────────────────── -->
    <p v-if="tagline" class="tagline">{{ tagline }}</p>

    <!-- ── Rubriques ──────────────────────────────────────────────────────────── -->
    <section class="rubriques">
      <h2 class="section-title">{{ $t('evenement.rubriques-title') }}</h2>
      <div class="list">
        <EventRubriqueItem
          v-for="rubrique in rubriques"
          :key="rubrique.key"
          :rubrique-key="rubrique.key"
          :count="rubrique.reservations.length"
          @click="$emit('rubrique', rubrique.key)"
        />
      </div>
    </section>

    <slot />
  </div>
</template>

<script setup lang="ts">
// Event board, commun au parcours public (/evenement, événement local) et à l'espace connecté
// (/app/events/:id sur mobile, événement en base) : couverture, accroche et rubriques. La page
// fournit les données et décide de la navigation (réglages, rubrique).
import { CalendarEventIcon, GroupIcon, MapPin2Icon, SettingsIcon } from '@remixicons/vue/line'
import EventRubriqueItem from '~/components/evenement/EventRubriqueItem.vue'
import type { EventMeta } from '~/data/evenement/domain/EventMeta'
import type { EventRubrique } from '~/data/evenement/domain/EventRubrique'
import { formatDate } from '~/utils/dateUtils'
import { EVENT_TYPE_TAGLINES } from '~/utils/eventTypes'

// Champs affichés : communs à l'événement en base (EventMeta) et à l'événement local (LocalEvent,
// qui reprend les mêmes noms ; son type peut être null tant qu'il n'est pas choisi).
type BoardEventMeta = Pick<EventMeta, 'title' | 'date' | 'ville' | 'nbInvites'> & {
  eventType?: string | null
}

const props = defineProps<{
  eventMeta: BoardEventMeta
  // Rubriques dans l'ordre d'affichage.
  rubriques: EventRubrique[]
  coverImage: string
}>()

defineEmits<{
  settings: []
  rubrique: [rubriqueKey: string]
}>()

const tagline = computed(() => EVENT_TYPE_TAGLINES[props.eventMeta.eventType ?? ''])

// ── Parallax ───────────────────────────────────────────────────────────────────
const coverRef = ref<HTMLElement | null>(null)
let rafId: number | null = null

function onScroll() {
  if (rafId !== null) return
  rafId = requestAnimationFrame(() => {
    if (coverRef.value) {
      coverRef.value.style.backgroundPositionY = `calc(50% + ${window.scrollY * 0.4}px)`
    }
    rafId = null
  })
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  if (rafId !== null) cancelAnimationFrame(rafId)
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.board {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: 0 $spacing-m $spacing-l;
  background-color: $surface-white;

  @media (min-width: $breakpoint-desktop) {
    width: 100%;
    max-width: 45rem;
    margin: 0 auto;
    padding-bottom: $spacing-xl;
  }

  // ── Couverture ─────────────────────────────────────────────────────────────
  .cover {
    display: flex;
    position: relative;
    height: 200px;
    margin: 0 (-$spacing-m);
    background-size: cover;
    background-position: center;
    align-items: flex-end;
    padding: $spacing-m;
    @media (min-width: $breakpoint-desktop) {
      height: 33vh;
      width: 100vw;
      left: 50%;
      right: 50%;
      margin-left: -50vw;
      margin-right: -50vw;
      padding: $spacing-l $spacing-xl;
    }

    .overlay {
      position: absolute;
      inset: 0;
      background: linear-gradient(to bottom, rgba(47, 42, 37, 0.1), rgba(47, 42, 37, 0.65));
      pointer-events: none;
    }

    .settings-btn {
      position: absolute;
      top: $spacing-m;
      right: $spacing-m;
      z-index: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 2rem;
      height: 2rem;
      border: none;
      border-radius: 50%;
      background: rgba(0, 0, 0, 0.25);
      color: rgba(255, 255, 255, 0.9);
      cursor: pointer;
      backdrop-filter: blur(4px);

      .settings-icon {
        width: 1.125rem;
        height: 1.125rem;
      }
    }

    .cover-content {
      position: relative;
      display: flex;
      flex-direction: column;
      gap: $spacing-xs;
    }

    .title {
      margin: 0;
      font-family: 'Cormorant Garamond', serif;
      font-size: 30px;
      font-weight: 600;
      color: #fff;
      line-height: 1.1;
      text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);

      @media (min-width: $breakpoint-desktop) {
        font-size: 42px;
      }
    }

    .info-lines {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .info-line {
      display: inline-flex;
      align-items: center;
      gap: 0.375rem;
      font-family: 'Inter', sans-serif;
      font-size: 0.8rem;
      font-weight: 500;
      color: rgba(255, 255, 255, 0.92);
      text-shadow: 0 1px 3px rgba(0, 0, 0, 0.35);

      .icon {
        width: 0.9rem;
        height: 0.9rem;
        flex-shrink: 0;
      }
    }
  }

  // ── Formule d'accroche ─────────────────────────────────────────────────────
  .tagline {
    margin: 0;
    font-family: 'Cormorant Garamond', serif;
    font-style: italic;
    font-size: 1.3rem;
    color: $brand-primary;
    text-align: center;
  }

  // ── Rubriques ───────────────────────────────────────────────────────────────
  .rubriques {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;

    .section-title {
      margin: 0;
      font-family: 'Inter', sans-serif;
      font-size: 0.7rem;
      font-weight: 700;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: $text-secondary;
    }

    .list {
      display: flex;
      flex-direction: column;
      margin: 0 (-$spacing-m);

      @media (min-width: $breakpoint-desktop) {
        margin: 0;
      }
    }
  }
}
</style>
