<template>
  <div class="home">
    <section class="search-form">
      <!-- title -->
      <div class="hero">
        <p class="title">
          <span class="title-thin">{{ $t('landing.search-banner.title-part-1') }}</span>
          <span class="title-bold">{{ $t('landing.search-banner.title-part-2') }}</span>
        </p>
        <h3 class="tagline">
          <template v-if="currentFlow === 'new-event'">
            <span>{{ $t('landing.search-banner.tagline-new-event') }}</span>
          </template>
          <template v-else>
            <span class="tagline-mobile">{{ $t('landing.search-banner.tagline-mobile') }}</span>
            <span class="tagline-desktop">{{ $t('landing.search-banner.tagline-desktop') }}</span>
          </template>
        </h3>
      </div>

      <!-- composants -->
      <div class="inputs">
        <SgiltDatePicker placeholder="Votre date" v-model="dateFilter" :disabled="isLocked" />

        <SgiltSelect :options="selectOptions" v-model="selectedOption" :disabled="isLocked">
          <template v-slot:left-icon> <IconConfetti :size="20" /> </template>
        </SgiltSelect>
        <SgiltHeroButton class="submit_button" @click="launch">
          {{ $t('landing.search-banner.cta') }}
        </SgiltHeroButton>
      </div>
    </section>
    <section class="photo-layer" aria-hidden="true"></section>
    <div class="app-background"></div>
  </div>
</template>

<script setup lang="ts">
import SgiltHeroButton from '~/components/basics/buttons/SgiltHeroButton.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import SgiltSelect from '~/components/basics/inputs/SgiltSelect.vue'
import IconConfetti from '~/components/icons/IconConfetti.vue'
import { EVENT_TYPE_OPTIONS } from '~/types/demande'
import { toISODate } from '~/utils/dateUtils'

useHead({ title: 'Sgilt (Beta)' })

const { showOnboarding } = useSearchUi()
const { state } = useDemande()
const { currentFlow, flowPayload } = useFlow()

const { t } = useI18n()

const selectOptions = [
  { label: t('landing.search-banner.your-event'), value: '1' },
  ...EVENT_TYPE_OPTIONS.map((o) => ({ label: `${o.emoji} ${o.label}`, value: o.value })),
]

const isLocked = computed(() => currentFlow.value === 'add-prestataire')

const dateFilter = ref<Date | undefined>(undefined)
const selectedOption = ref(selectOptions[0]!.value)

onMounted(() => {
  showOnboarding.value = true
  if (isLocked.value) {
    if (flowPayload.value?.date) dateFilter.value = new Date(flowPayload.value.date)
    if (flowPayload.value?.eventType) selectedOption.value = flowPayload.value.eventType
  }
})

const launch = () => {
  state.eventType = selectedOption.value !== '1' ? selectedOption.value : ''
  navigateTo({
    path: '/search',
    query: { date: dateFilter.value ? toISODate(dateFilter.value) : undefined },
  })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ─── Tokens locaux ────────────────────────────────────────────────────────────
$background: white;
$padding-top: 0.75rem;
$hero-color: #000000;

$search-form-padding: $spacing-l $spacing-m 0;
$search-form-gap: clamp(1rem, 3vh, 3rem);

$title-thin-font-weight: 600;
$title-thin-font-size: 2.5rem;
$title-thin-line-height: 2.75rem;

$title-bold-font-weight: 900;
$title-bold-font-size: 3.2rem;
$title-bold-line-height: 3rem;
$title-bold-letter-spacing: 0.02em;
$title-bold-margin-bottom: 0.875rem;

$tagline-font-weight: 500;
$tagline-font-size: 1.125rem;
$tagline-line-height: 1.25;
$tagline-color: $text-secondary;
$tagline-max-width: 36rem;

$inputs-gap: 0.875rem;
$inputs-width: 90%;
$inputs-max-width: 30rem;
$inputs-font-size: 1.125rem;
$inputs-height: 3rem;

$photo-filter: brightness(1.03) contrast(1.03) saturate(1.06);

// ─── Composant ────────────────────────────────────────────────────────────────
.home {
  position: relative;
  width: 100%;
  min-height: calc(100dvh - $app-header-height);
  overflow: hidden;
  background: $background;
  padding-top: $padding-top;

  // Grille mobile : 2 zones empilées
  display: grid;
  grid-template-rows: 50% 50%;
  grid-template-columns: 1fr;
  grid-template-areas:
    'content'
    'visual';

  @media (max-width: #{$breakpoint-desktop - 1px}) {
    // Cacher l'ombre du header uniquement sur mobile
    :global(.app-header) {
      box-shadow: none;
    }
  }

  @media (min-width: $breakpoint-desktop) {
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    padding-top: 0;
    color: #fff;
  }

  // ── Photo de fond ──────────────────────────────────────────────────────────
  .photo-layer {
    grid-area: visual;
    z-index: 1;
    filter: $photo-filter;
    background-image: url('/images/hero-party.png');
    background-size: cover;
    background-repeat: no-repeat;
    background-position: 50% 30%;
    -webkit-mask-image: linear-gradient(to bottom, transparent 0%, #000 18%, #000 100%);
    mask-image: linear-gradient(to bottom, transparent 0%, #000 28%, #000 100%);
    -webkit-mask-repeat: no-repeat;
    mask-repeat: no-repeat;
    -webkit-mask-size: 100% 100%;
    mask-size: 100% 100%;
    pointer-events: none;

    @media (min-width: $breakpoint-desktop) {
      position: absolute;
      inset: 0;
      z-index: 0;
      background-image: url('/images/hero-party-desktop.png');
      -webkit-mask-image: none;
      mask-image: none;
      filter: brightness(1) contrast(1.03) saturate(1.06);

      &::after {
        content: '';
        position: absolute;
        inset: 0;
        pointer-events: none;
      }
    }
  }

  // ── Zone de recherche ──────────────────────────────────────────────────────
  .search-form {
    z-index: 2;
    grid-area: content;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: $search-form-padding;
    justify-content: center;
    gap: $search-form-gap;

    @media (min-width: $breakpoint-desktop) {
      height: 50vh;
      padding-top: 6%;
      padding-bottom: 3%;
      justify-content: space-between;
      border-radius: 0 0 10px 10px;
      overflow: hidden;

      // Glassmorphism
      background: linear-gradient(
        180deg,
        rgba(255, 255, 255, 0.25) 0%,
        rgba(255, 255, 255, 0.1) 100%
      );
      backdrop-filter: blur(3px) saturate(1.15);
      -webkit-backdrop-filter: blur(3px) saturate(1.15);
      border: 1px solid rgba(255, 255, 255, 0.42);
      box-shadow:
        0 10px 40px rgba(0, 0, 0, 0.15),
        inset 0 1px 0 rgba(255, 255, 255, 0.4);

      &::before {
        content: '';
        position: absolute;
        inset: 0;
        background: radial-gradient(
          900px 220px at 50% 0%,
          rgba($color-accent, 0.12),
          transparent 60%
        );
        pointer-events: none;
      }
    }
  }

  // ── Hero (titre + tagline) ─────────────────────────────────────────────────
  .hero {
    text-align: center;
    color: $color-primary;
    display: flex;
    flex-direction: column;
    align-items: center;

    @media (min-width: $breakpoint-desktop) {
      transform: translateY(-0.5rem);
      width: 100%;
      color: #fff;
      text-shadow:
        0 2px 10px rgba(0, 0, 0, 0.35),
        0 14px 40px rgba(0, 0, 0, 0.22);
      padding: 0;
      margin: 0;
      gap: 0.6rem;
    }
  }

  .title {
    display: flex;
    flex-direction: column;
    color: $hero-color;

    @media (min-width: $breakpoint-desktop) {
      flex-direction: row;
      color: inherit;
      align-items: baseline;
      gap: 0.8rem;
      margin: 0;
      text-shadow: 0 6px 18px rgba(0, 0, 0, 0.18);
    }
  }

  .title-thin {
    font-weight: $title-thin-font-weight;
    font-size: $title-thin-font-size;
    line-height: $title-thin-line-height;
    font-family: 'Cormorant Garamond', serif;

    @media (min-width: $breakpoint-desktop) {
      font-size: 3.2rem;
      line-height: 1.05;
      color: $color-primary;
    }
  }

  .title-bold {
    font-weight: $title-bold-font-weight;
    font-size: $title-bold-font-size;
    line-height: $title-bold-line-height;
    letter-spacing: $title-bold-letter-spacing;
    margin-bottom: $title-bold-margin-bottom;

    @media (min-width: $breakpoint-desktop) {
      font-weight: 800;
      font-size: 4rem;
      line-height: 1.05;
      letter-spacing: 0.02rem;
      margin-top: 0.2rem;
      margin-bottom: 0;
      color: $color-accent;
      text-shadow:
        0 0 12px rgba(255, 210, 0, 0.4),
        0 8px 24px rgba(0, 0, 0, 0.2);
    }
  }

  .tagline {
    font-weight: $tagline-font-weight;
    font-size: $tagline-font-size;
    line-height: $tagline-line-height;
    color: $tagline-color;
    max-width: $tagline-max-width;

    .tagline-mobile {
      display: block;
    }

    .tagline-desktop {
      display: none;
    }

    @media (min-width: $breakpoint-desktop) {
      margin: 0;
      padding: 0;
      width: 100%;
      max-width: 100%;
      font-weight: 400;
      font-size: clamp(1rem, 1.2vw, 1.15rem);
      line-height: 1.3;
      opacity: 0.88;
      color: rgba(0, 0, 0, 0.75);

      .tagline-mobile {
        display: none;
      }

      .tagline-desktop {
        display: block;
        letter-spacing: 1px;
      }
    }
  }

  // ── Inputs ─────────────────────────────────────────────────────────────────
  .inputs {
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $inputs-gap;
    width: $inputs-width;
    max-width: $inputs-max-width;
    font-size: $inputs-font-size;

    .sgilt-button {
      width: 100%;
    }

    > * {
      height: $inputs-height;
    }

    @media (min-width: $breakpoint-desktop) {
      flex-direction: row;
      gap: 1.1rem;
      justify-content: center;
      height: unset;
      width: unset;
      max-width: unset;
      margin: 0;
      padding: 0.3rem;
      font-size: 1rem;
      border-radius: 2rem;
      background: radial-gradient(
        1200px 200px at 50% -20%,
        rgba(255, 190, 60, 0.18),
        transparent 60%
      );
      border: 1px solid rgba(255, 255, 255, 0.42);
      box-shadow:
        0 18px 50px rgba(0, 0, 0, 0.18),
        inset 0 1px 0 rgba(255, 255, 255, 0.55);
      filter: drop-shadow(0 14px 40px rgba(0, 0, 0, 0.22));

      > * {
        height: 3rem;
        min-width: 17rem;
        max-width: 17rem;
        font-size: 1.1rem;
        backdrop-filter: blur(6px);
      }

      .sgilt-button {
        width: unset;
        box-shadow:
          0 8px 20px rgba(0, 0, 0, 0.25),
          0 2px 6px rgba(0, 0, 0, 0.15);
      }
    }
  }

  // ── Background décoratif ───────────────────────────────────────────────────
  .app-background {
    position: absolute;
    z-index: 0;
    inset: 0;
    mask-image: none;
    -webkit-mask-image: none;
    pointer-events: none;
  }
}
</style>
