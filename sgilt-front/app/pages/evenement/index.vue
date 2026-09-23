<template>
  <div class="evenement">
    <!-- ── Couverture ─────────────────────────────────────────────────────────── -->
    <div ref="coverRef" class="cover" :style="{ backgroundImage: `url(${coverImage})` }">
      <div class="overlay" />
      <button
        class="settings-btn"
        type="button"
        :aria-label="$t('evenement.settings-aria')"
        @click="onSettingsClick"
      >
        <SettingsIcon class="settings-icon" />
      </button>
      <div class="cover-content">
        <h1 class="title">{{ localEvent.title }}</h1>
        <div class="info-lines">
          <span v-if="localEvent.date" class="info-line">
            <CalendarEventIcon class="icon" />{{ formatDate(localEvent.date) }}
          </span>
          <span v-if="localEvent.ville" class="info-line">
            <MapPin2Icon class="icon" />{{ localEvent.ville }}
          </span>
          <span v-if="localEvent.nbInvites" class="info-line">
            <GroupIcon class="icon" />{{ localEvent.nbInvites }}
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
          v-for="rubrique in localEvent.rubriques"
          :key="rubrique.key"
          :rubrique="rubrique"
          @click="navigateTo(`/evenement/${rubrique.key}`)"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { CalendarEventIcon, GroupIcon, MapPin2Icon, SettingsIcon } from '@remixicons/vue/line'
import EventRubriqueItem from '~/components/evenement/EventRubriqueItem.vue'
import { formatDate } from '~/utils/dateUtils'
import { resolveEventCover } from '~/utils/eventCovers'
import { EVENT_TYPE_TAGLINES } from '~/utils/eventTypes'

definePageMeta({ layout: 'evenement' })

const { t } = useI18n()
useHead({ title: t('evenement.page-title') })

const { localEvent, initMariage } = useLocalEvent()

// Première arrivée : le preset Mariage est injecté dans le state local (pas d'appel réseau).
initMariage()

// ── Couverture ───────────────────────────────────────────────────────────────
// Même banque d'images que l'event board /app (fallback par type, jusqu'à 'autre').
const { toUrl } = useImageUrl()
const coverImage = computed(() =>
  resolveEventCover({ coverImage: null, eventType: localEvent.eventType ?? undefined }, toUrl),
)

const tagline = computed(() => EVENT_TYPE_TAGLINES[localEvent.eventType ?? ''])

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

function onSettingsClick() {
  navigateTo('/evenement/parametres')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.evenement {
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
