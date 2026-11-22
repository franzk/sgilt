<!--
  Section « frise » de la landing (2e position, sous l'écran d'arrivée) : un titre et les 3 étapes
  du parcours.
  Les étapes apparaissent l'une après l'autre quand la frise entre à l'écran ; sans JS ou avec
  « réduire les animations », elles sont affichées d'emblée.
-->
<template>
  <section class="home-start" :class="{ armed, revealed }">
    <h2 class="title">{{ $t('landing-page.start.title') }}</h2>

    <ol class="steps">
      <li ref="firstStepRef" class="step" style="--step-index: 0">
        <div class="marker"><FileList3Icon class="icon" /></div>
        <div class="step-text">
          <h3>{{ $t('landing-page.start.step-1-title') }}</h3>
          <p>{{ $t('landing-page.start.step-1-description') }}</p>
        </div>
      </li>
      <li class="step" style="--step-index: 1">
        <div class="marker"><CalendarEventIcon class="icon" /></div>
        <div class="step-text">
          <h3>{{ $t('landing-page.start.step-2-title') }}</h3>
          <p>{{ $t('landing-page.start.step-2-description') }}</p>
        </div>
      </li>
      <li class="step" style="--step-index: 2">
        <div class="marker"><CheckboxCircleIcon class="icon" /></div>
        <div class="step-text">
          <h3>{{ $t('landing-page.start.step-3-title') }}</h3>
          <p>{{ $t('landing-page.start.step-3-description') }}</p>
        </div>
      </li>
    </ol>
  </section>
</template>

<script setup lang="ts">
import { FileList3Icon, CalendarEventIcon, CheckboxCircleIcon } from '@remixicons/vue/line'
import { useIntersectionObserver } from '@vueuse/core'

// L'animation part dès que la première étape entre à l'écran.
const firstStepRef = ref<HTMLElement | null>(null)
// Armé seulement côté client : sans JS, les étapes restent visibles.
const armed = ref<boolean>(false)
const revealed = ref<boolean>(false)

onMounted((): void => {
  armed.value = true
})

const { stop } = useIntersectionObserver(
  firstStepRef,
  ([entry]) => {
    if (!entry?.isIntersecting) return
    revealed.value = true
    stop()
  },
  { threshold: 0 },
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-start {
  padding: $spacing-xxxl $section-padding-x;
  // Fond blanc entre le hero et la section idée (fond doux) : les sections alternent.
  background: $surface-white;

  .title {
    margin: 0 0 $spacing-xl;
    font-family: 'Cormorant Garamond', serif;
    font-weight: $font-weight-medium;
    font-size: clamp(1.8rem, 4vw, 2.5rem);
    color: $text-primary;

    @media (min-width: $breakpoint-desktop) {
      margin-bottom: $spacing-xxl;
      text-align: center;
    }
  }

  // ── 3 étapes ──────────────────────────────────────────────────────────────
  .steps {
    list-style: none;
    margin: 0;
    padding: 0;
    display: grid;
    grid-template-columns: 1fr;
    gap: $spacing-xxl;

    @media (min-width: $breakpoint-desktop) {
      grid-template-columns: repeat(3, 1fr);
      // Le fond de la section couvre toute la largeur, seules les étapes sont centrées.
      max-width: $container-max-width;
      margin: 0 auto;
      gap: $spacing-l;
    }
  }

  .step {
    position: relative;
    transition:
      opacity 1s ease,
      transform 1s ease;
    transition-delay: calc(var(--step-index) * 0.5s);
    display: flex;
    align-items: flex-start;
    text-align: left;
    gap: $spacing-m;

    // Ligne pointillée verticale entre les repères, mobile (marqueur + texte
    // en ligne) — devient horizontale sur desktop (steps côte à côte).
    &:not(:last-child)::after {
      content: '';
      position: absolute;
      top: 4.5rem;
      bottom: calc(-1 * #{$spacing-xxl}); // suit le gap mobile de .steps
      left: 2.25rem;
      border-left: 2px dashed $divider-color;
    }

    @media (min-width: $breakpoint-desktop) {
      flex-direction: column;
      align-items: center;
      text-align: center;
      gap: $spacing-xs;

      &:not(:last-child)::after {
        top: 2.25rem;
        bottom: auto;
        left: calc(50% + 3rem);
        width: calc(100% - 6rem);
        border-left: none;
        border-top: 2px dashed $divider-color;
      }
    }

    .step-text {
      display: flex;
      flex-direction: column;
      gap: $spacing-xs;

      // Desktop : le paragraphe (max-width en ch) se centre sous le titre, plus large que lui.
      @media (min-width: $breakpoint-desktop) {
        align-items: center;
      }
    }

    h3 {
      margin: 0;
      font-size: $font-size-md;
      font-weight: $font-weight-semibold;
      color: $text-primary;
    }

    p {
      margin: 0;
      max-width: 26ch;
      color: $text-secondary;
      font-size: $font-size-sm;
      line-height: $line-height-relaxed;
    }
  }

  // Apparition échelonnée des étapes (voir --step-index sur chaque <li>).
  &.armed:not(.revealed) .step {
    opacity: 0;
    transform: translateY(1rem);
  }

  @media (prefers-reduced-motion: reduce) {
    &.armed:not(.revealed) .step {
      opacity: 1;
      transform: none;
    }

    .step {
      transition: none;
    }
  }

  .marker {
    position: relative;
    z-index: 1;
    flex-shrink: 0;
    width: 4.5rem;
    height: 4.5rem;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba($brand-accent, 0.18);

    .icon {
      width: 2rem;
      height: 2rem;
      color: $brand-primary;
    }
  }
}
</style>
