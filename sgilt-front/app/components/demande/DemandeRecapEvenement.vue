<template>
  <div class="recap-evenement">
    <div class="column">
      <!-- ── En-tête ─────────────────────────────────────────────────────────── -->
      <div class="top-row">
        <button class="back" type="button" @click="navigateTo(`/${slug}`)">
          <ArrowLeftIcon class="icon" aria-hidden="true" />
          {{ $t('tunnel.recap-evenement.back') }}
        </button>
        <span class="prestataire">
          <span class="prestataire-name">
            {{ $t('tunnel.recap-evenement.request-to', { name: prestataireName }) }}
          </span>
          <SgiltImage
            class="prestataire-avatar"
            :src="prestataireImage"
            :alt="prestataireName"
            width="96"
            height="96"
          />
        </span>
      </div>

      <h1 class="title">{{ $t('tunnel.recap-evenement.title') }}</h1>
      <p class="subtitle">
        {{ $t('tunnel.recap-evenement.subtitle', { name: prestataireName }) }}
      </p>

      <!-- Le récap est une vue de l'événement : chaque panneau validé l'écrit directement. -->
      <EventInfoCards
        ref="infoCards"
        class="cards"
        :event="localEvent"
        essentials-required
        @update="Object.assign(localEvent, $event)"
      />

      <!-- ── Conseil ─────────────────────────────────────────────────────────── -->
      <div class="tip">
        <LightbulbIcon class="icon" aria-hidden="true" />
        <span class="tip-text">
          <strong class="tip-title">{{ $t('tunnel.recap-evenement.tip-title') }}</strong>
          {{ $t('tunnel.recap-evenement.tip-text') }}
        </span>
      </div>

      <button class="cta" type="button" @click="onContinue">
        {{ $t('tunnel.recap-evenement.continue') }}
        <ArrowRightIcon class="icon" aria-hidden="true" />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeftIcon, ArrowRightIcon, LightbulbIcon } from '@remixicons/vue/line'
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import EventInfoCards from '~/components/evenement/EventInfoCards.vue'

defineProps<{
  prestataireName: string
  prestataireImage: string
  slug: string
}>()

const { localEvent } = useLocalEvent()

const infoCards = useTemplateRef<InstanceType<typeof EventInfoCards>>('infoCards')

function onContinue() {
  if (!infoCards.value?.validate()) return
  // Écran 2 (coordonnées + validation) : maquette à venir.
  console.log('stay tuned')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.recap-evenement {
  display: flex;
  justify-content: center;
  flex: 1;
  background: $surface-white;

  .column {
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 30rem;
    padding: $spacing-m $section-padding-x $spacing-xl;

    .top-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: $spacing-m;
      margin-bottom: $spacing-l;

      .back {
        display: inline-flex;
        align-items: center;
        gap: $spacing-xxs;
        padding: 0;
        border: none;
        background: none;
        color: $text-primary;
        font-family: inherit;
        font-size: $font-size-sm;
        cursor: pointer;

        .icon {
          width: 1.125rem;
          height: 1.125rem;
        }
      }

      .prestataire {
        display: flex;
        align-items: center;
        gap: $spacing-xs;
        min-width: 0;

        .prestataire-name {
          overflow: hidden;
          color: $text-secondary;
          font-size: $font-size-xs;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .prestataire-avatar {
          flex-shrink: 0;
          width: 2.5rem;
          height: 2.5rem;
          border-radius: 50%;
          overflow: hidden;
        }
      }
    }

    .title {
      margin: 0;
      color: $text-primary;
      font-family: 'Cormorant Garamond', serif;
      font-size: clamp(1.75rem, 7vw, 2.25rem);
      font-weight: $font-weight-medium;
      line-height: 1.2;
    }

    .subtitle {
      margin: $spacing-xs 0 0;
      color: $text-secondary;
      font-size: $font-size-md;
      line-height: $line-height-normal;
    }

    .cards {
      margin-top: $spacing-l;
    }

    // ── Conseil ────────────────────────────────────────────────────────────────
    .tip {
      display: flex;
      align-items: flex-start;
      gap: $spacing-s;
      margin-top: $spacing-l;
      padding: $spacing-m;
      border-radius: $radius-lg;
      background: rgba($brand-accent, 0.1);

      .icon {
        flex-shrink: 0;
        width: 1.75rem;
        height: 1.75rem;
        color: $brand-accent;
      }

      .tip-text {
        color: $text-secondary;
        font-size: $font-size-xs;
        line-height: $line-height-normal;

        .tip-title {
          display: block;
          color: $text-primary;
          font-size: $font-size-sm;
        }
      }
    }

    // Même bouton que le CTA du tunnel /organisation.
    .cta {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: $spacing-xs;
      width: 100%;
      height: 3.25rem;
      margin-top: $spacing-l;
      border: none;
      border-radius: 9999px;
      background: $brand-accent;
      color: $brand-primary;
      font-family: inherit;
      font-size: $font-size-md;
      font-weight: $font-weight-bold;
      cursor: pointer;
      box-shadow: 0 0.25rem 0.625rem rgba($brand-primary, 0.18);

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
