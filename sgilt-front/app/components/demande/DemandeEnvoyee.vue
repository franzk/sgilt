<template>
  <div class="envoyee">
    <div class="column">
      <img class="illustration" src="/images/envoi-email.png" alt="" />

      <h1 class="title">{{ $t('tunnel.envoyee.title') }}</h1>
      <p class="text">{{ $t('tunnel.envoyee.text') }}</p>
      <p class="text-then">
        {{ $t('tunnel.envoyee.text-then', { name: summary.prestataireName }) }}
      </p>

      <!-- ── E-mail envoyé à ─────────────────────────────────────────────────── -->
      <div class="email-card">
        <span class="email-head">
          <span class="email-icon" aria-hidden="true">
            <MailIcon class="icon" />
          </span>
          <span class="eyebrow">{{ $t('tunnel.envoyee.email-sent-to') }}</span>
        </span>
        <span class="email">{{ email }}</span>
      </div>

      <DemandeSummaryCard
        class="summary"
        :summary="summary"
        :eyebrow="$t('tunnel.envoyee.request-sent-to')"
      />

      <button class="home" type="button" @click="navigateTo('/')">
        <ArrowLeftIcon class="icon" aria-hidden="true" />
        {{ $t('tunnel.envoyee.home') }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeftIcon, MailIcon } from '@remixicons/vue/line'
import DemandeSummaryCard from '~/components/demande/DemandeSummaryCard.vue'
import type { DemandeSummary } from '~/types/demande'

defineProps<{
  // Copie figée au moment de l'envoi : l'événement local et la demande sont déjà vidés.
  summary: DemandeSummary
  email: string
}>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.envoyee {
  display: flex;
  justify-content: center;
  flex: 1;
  background: $surface-white;

  .column {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
    max-width: 30rem;
    padding: $spacing-l $section-padding-x $spacing-xl;
    text-align: center;

    // L'image a de larges marges transparentes autour du dessin : pleine largeur de colonne.
    .illustration {
      display: block;
      width: 100%;
      max-width: 22rem;
      height: auto;
    }

    .title {
      margin: $spacing-s 0 0;
      color: $text-primary;
      font-family: 'Cormorant Garamond', serif;
      font-size: clamp(2rem, 8vw, 2.5rem);
      font-weight: $font-weight-semibold;
      line-height: 1.2;
    }

    .text {
      margin: $spacing-s 0 0;
      color: $text-secondary;
      font-size: $font-size-md;
      line-height: $line-height-normal;
    }

    .text-then {
      margin: $spacing-s 0 0;
      color: $text-secondary;
      font-size: $font-size-sm;
    }

    // ── E-mail envoyé à ────────────────────────────────────────────────────────
    // L'adresse a toute la largeur de la carte, sous l'icône et le titre.
    .email-card {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;
      width: 100%;
      margin-top: $spacing-l;
      padding: $spacing-m;
      border-radius: $radius-lg;
      background: rgba($brand-accent, 0.08);
      text-align: left;

      .email-head {
        display: flex;
        align-items: center;
        gap: $spacing-s;

        .email-icon {
          display: flex;
          flex-shrink: 0;
          align-items: center;
          justify-content: center;
          width: 2.5rem;
          height: 2.5rem;
          border-radius: 50%;
          background: rgba($brand-accent, 0.18);

          .icon {
            width: 1.25rem;
            height: 1.25rem;
            color: $brand-accent;
          }
        }

        .eyebrow {
          color: $text-secondary;
          font-size: $font-size-xs;
          font-weight: $font-weight-semibold;
          letter-spacing: 0.14em;
          text-transform: uppercase;
        }
      }

      .email {
        padding: $spacing-xs $spacing-m;
        border-radius: 9999px;
        background: rgba($brand-accent, 0.12);
        color: $text-primary;
        font-size: $font-size-sm;
        text-align: center;
        // Coupure seulement si l'adresse dépasse vraiment toute la largeur.
        overflow-wrap: anywhere;
      }
    }

    .summary {
      width: 100%;
      margin-top: $spacing-m;
      text-align: left;
    }

    .home {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: $spacing-xs;
      width: 100%;
      height: 3.25rem;
      margin-top: $spacing-l;
      border: 1.5px solid $divider-color;
      border-radius: 9999px;
      background: $surface-white;
      color: $text-primary;
      font-family: inherit;
      font-size: $font-size-sm;
      cursor: pointer;

      &:focus-visible {
        outline: 3px solid $brand-primary;
        outline-offset: 4px;
      }

      .icon {
        width: 1.25rem;
        height: 1.25rem;
      }
    }
  }
}
</style>
