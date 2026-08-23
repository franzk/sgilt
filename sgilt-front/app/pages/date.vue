<template>
  <LandingHeroScreen
    :title="$t('landing.date-banner.title')"
    :highlighted-subtext="$t('landing.date-banner.title-highlight')"
    :subtitle="$t('landing.date-banner.tagline-desktop')"
  >
    <div class="date-content">
      <div class="date-picker-wrap">
        <SgiltDatePicker v-model="date" inline fullwidth />
      </div>

      <div class="actions">
        <button class="cta-button" type="button">{{ $t('landing.date-banner.cta') }}</button>
        <button class="skip-link" type="button">{{ $t('landing.date-banner.skip-link') }}</button>
      </div>
    </div>
  </LandingHeroScreen>
</template>

<script setup lang="ts">
import LandingHeroScreen from '~/components/landing/LandingHeroScreen.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'

useHead({ title: 'C\'est pour quand ? - Sgilt' })

const date = ref<Date>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$content-width: 100%;
$content-max-width: 30rem;

.date-content {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: clamp(0.5rem, 1.5vh, 1.5rem);
}

// ── Calendrier ──────────────────────────────────────────────────────────────
.date-picker-wrap {
  width: $content-width;
  max-width: $content-max-width;

  @media (min-width: $breakpoint-desktop) {
    max-width: none;
  }

  // Réglage propre à cette page : le calendrier respire plus à mesure que
  // l'écran grandit. Le mode fullwidth lui-même (taille des cellules) est
  // porté par SgiltDatePicker (prop `fullwidth`), réutilisable ailleurs.
  :deep(.dp__theme_light) {
    --dp-menu-padding: #{$spacing-m};
  }

  @media (min-width: $breakpoint-desktop) {
    :deep(.dp__theme_light) {
      --dp-menu-padding: #{$spacing-l};
    }
  }
}

// ── Actions ─────────────────────────────────────────────────────────────────
.actions {
  width: $content-width;
  max-width: $content-max-width;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-m;

  @media (min-width: $breakpoint-desktop) {
    max-width: none;
  }
}

.cta-button {
  width: 100%;
  height: 3.25rem;
  border: none;
  border-radius: 999px;
  background: $color-accent;
  color: #000000;
  font-size: 1.125rem;
  font-weight: 700;
  cursor: pointer;
}

.skip-link {
  width: 100%;
  height: 3.25rem;
  margin: 0;
  border: 1px solid $divider-color;
  border-radius: 999px;
  background: $surface-white;
  color: $text-primary;
  font-size: 1.125rem;
  font-weight: 600;
  cursor: pointer;
}
</style>
