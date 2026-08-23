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
  min-height: 0;
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
  // Déclarée ici (pas en :deep) pour rester lisible depuis min-height
  // ci-dessous — une custom property posée sur un ancestor cascade quand
  // même normalement vers l'intérieur du composant.
  --dp-menu-padding: #{$spacing-m};

  @media (min-width: $breakpoint-desktop) {
    --dp-menu-padding: #{$spacing-l};
  }

  // La taille des cellules suit la hauteur d'écran (plafonnée à 35px, la
  // taille par défaut de la lib — donc rien ne change visuellement tant que
  // l'écran est assez haut, ça ne se déclenche que sur un viewport court).
  // Un simple plafond en vh sur la réservation ci-dessous ne suffisait pas :
  // il limitait le chiffre "réservé" mais pas le rendu réel du calendrier —
  // sur un mois à 6 lignes, le contenu poussait quand même au-delà du
  // plafond, redevenant incohérent d'un mois à l'autre. En rendant la cause
  // (taille des cellules) réactive plutôt que la conséquence (réservation),
  // réservation et rendu réel restent synchronisés à toute hauteur d'écran.
  --dp-cell-size: clamp(1.5rem, 5vh, 2.1875rem);

  // Un mois affiche 5 ou 6 lignes de semaines selon le calendrier. Plutôt que
  // de forcer l'intérieur du calendrier à s'étirer, on réserve directement ici
  // — sur le wrapper de la page — la place du cas maximal (6 lignes) ; le
  // calendrier garde sa taille naturelle et vit simplement dans ce slot, quitte
  // à laisser du vide en dessous pour un mois à 5 lignes. Calculé depuis les
  // variables de SgiltDatePicker plutôt qu'un chiffre en dur (35px/10px =
  // défauts de la lib pour --dp-month-year-row-height et --dp-row-margin).
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
