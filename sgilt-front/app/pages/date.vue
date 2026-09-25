<template>
  <LandingHeroScreen
    :eyebrow="$t('landing.date-banner.eyebrow')"
    :title="$t('landing.date-banner.title')"
    :highlighted-subtext="$t('landing.date-banner.title-highlight')"
    :subtitle="$t('landing.date-banner.tagline-desktop')"
  >
    <div class="date-content">
      <div class="date-picker-wrap">
        <SgiltDatePicker v-model="date" inline fullwidth />
      </div>

      <div class="actions">
        <p v-if="dateError" class="date-error">{{ $t('landing.date-banner.date-error') }}</p>
        <div class="action-buttons">
          <button class="cta-button" type="button" @click="confirmDate">
            {{ $t('landing.date-banner.cta') }}
          </button>
          <button class="skip-link" type="button" @click="skipDate">
            {{ $t('landing.date-banner.skip-link') }}
          </button>
        </div>
      </div>
    </div>
  </LandingHeroScreen>
</template>

<script setup lang="ts">
import LandingHeroScreen from '~/components/landing/LandingHeroScreen.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'

useHead({ title: 'C\'est pour quand ? - Sgilt' })

const { localEvent } = useLocalEvent()

const date = ref<Date>()
const dateError = ref(false)

function confirmDate() {
  if (!date.value) {
    dateError.value = true
    return
  }
  localEvent.date = date.value
  navigateTo('/commencer')
}

function skipDate() {
  localEvent.date = undefined
  navigateTo('/commencer')
}
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
  gap: 0;
  min-height: 0;
}

// ── Calendrier ──────────────────────────────────────────────────────────────
.date-picker-wrap {
  width: $content-width;
  max-width: $content-max-width;

  display: flex;
  flex-direction: column;

  --dp-menu-padding: #{$spacing-m};

  @media (min-width: $breakpoint-desktop) {
    --dp-menu-padding: #{$spacing-m};
  }


  --dp-cell-size: clamp(1.5rem, 5vh, 1.875rem);

  // le redimensionnement du calendrier selon le nombre de semaines dans le mois ne doit pas perturber
  // le layout de la page (saut de contenu, scroll vertical) :
  // on réserve la place maximale possible pour le calendrier.
  min-height: calc(
    2 * var(--dp-menu-padding) + var(--dp-month-year-row-height, 35px) +
      var(--dp-cell-size) + 6 * (var(--dp-cell-size) + 10px)
  );
}

// ── Actions ─────────────────────────────────────────────────────────────────
.actions {
  width: $content-width;
  max-width: $content-max-width;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-m;
  padding-bottom: $spacing-s;
}

.action-buttons {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  @media (min-width: $breakpoint-desktop) {
    flex-direction: row;

    // width:100% (pensé pour l'empilement mobile) ferait sinon lutter les
    // deux boutons pour toute la largeur de la ligne.
    > * {
      width: auto;
      flex: 1;
    }
  }
}

.date-error {
  margin: 0;
  color: $state-error;
  font-size: $font-size-sm;
  text-align: center;
}

.cta-button {
  width: 100%;
  height: 3.25rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 9999px;
  background: $brand-accent;
  color: $brand-primary;
  font-size: $font-size-md;
  font-weight: $font-weight-bold;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba($brand-primary, 0.18);
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba($brand-primary, 0.22);
  }

  &:focus-visible {
    outline: 3px solid $brand-primary;
    outline-offset: 4px;
  }
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
