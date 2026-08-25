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
import { toISODate } from '~/utils/dateUtils'

useHead({ title: 'C\'est pour quand ? - Sgilt' })

const { state } = useDemande()
const { showOnboarding, stateDate } = useSearchUi()

onMounted(() => {
  showOnboarding.value = true
})

const date = ref<Date>()
const dateError = ref(false)

function confirmDate() {
  if (!date.value) {
    dateError.value = true
    return
  }
  state.date = date.value
  navigateTo({ path: '/search', query: { date: toISODate(date.value) } })
}

function skipDate() {
  // Sécurité : évite qu'une date choisie puis abandonnée (retour arrière,
  // session précédente via le sessionStorage de useDemande) ne reste dans le
  // state alors que l'utilisateur vient de dire explicitement qu'il ne la
  // connaît pas encore. Deux states distinctes à vider : state.date
  // (useDemande, sert au tunnel de contact) et stateDate (useSearchUi, cache
  // qui fait vivre la date sur /search et les fiches prestataire au-delà de
  // la query string — à ne vider que sur cette action explicite, jamais à
  // chaque changement de route sous peine de casser ce report).
  state.date = undefined
  stateDate.value = undefined
  navigateTo('/search')
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
