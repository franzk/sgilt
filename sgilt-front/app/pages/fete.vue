<template>
  <LandingHeroScreen
    :title="$t('landing.search-banner.title')"
    :highlighted-subtext="$t('landing.search-banner.title-highlight')"
    :subtitle="$t('landing.search-banner.tagline-desktop')"
  >
    <div class="event-types">
      <div class="event-type-grid">
        <EventTypeCard
          v-for="eventType in EVENT_TYPE_CATALOG"
          :key="eventType.key"
          :type="eventType.key"
          :label="eventType.label"
          :icon="eventType.icon"
          @click="selectEventType(eventType.key)"
        />
      </div>

      <button type="button" class="inspire-link" @click="browseToSearch">
        {{ $t('landing.search-banner.inspire-link') }}
      </button>
    </div>
  </LandingHeroScreen>
</template>

<script setup lang="ts">
import LandingHeroScreen from '~/components/landing/LandingHeroScreen.vue'
import EventTypeCard from '~/components/cards/EventTypeCard.vue'
import { EVENT_TYPE_CATALOG } from '~/utils/eventTypes'

useHead({ title: "Qu'est-ce qu'on fête ? - Sgilt" })

const { showOnboarding } = useSearchUi()
const { state, reset } = useDemande()

onMounted(() => {
  showOnboarding.value = true
})

function selectEventType(key: string) {
  state.eventType = key
  navigateTo('/date')
}

function browseToSearch() {
  reset()
  navigateTo('/search')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$event-types-width: 100%;
$event-types-max-width: 30rem;
$event-type-grid-gap: $spacing-s;

// ── Types d'événement (grille de cartes + échappatoire) ──────────────────────
.event-types {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-l;
}

// ── Grille des types d'événement ──────────────────────────────────────────
.event-type-grid {
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $event-type-grid-gap;
  width: $event-types-width;
  max-width: $event-types-max-width;

  // Deuxième ligne du layout stacked : les 6 cartes alignées horizontalement
  // plutôt que la grille 2 colonnes / 3 lignes du mobile.
  @media (min-width: $breakpoint-desktop) {
    grid-template-columns: repeat(6, 1fr);
    max-width: none;
  }
}

// ── Échappatoire ("voir les prestataires") ─────────────────────────────────
.inspire-link {
  margin: 0;
  padding: $spacing-xs $spacing-m;
  border: none;
  background: rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  border-radius: 999px;
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.08);
  color: $text-secondary;
  font: inherit;
  font-size: $font-size-md;
  cursor: pointer;
}
</style>
