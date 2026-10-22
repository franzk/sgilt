<template>
  <div class="recap-evenement">
    <div class="column">
      <DemandeHeader
        class="header"
        :prestataire-name="prestataireName"
        :prestataire-image="prestataireImage"
        @back="navigateTo(`/${slug}`)"
      />

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
import { ArrowRightIcon, LightbulbIcon } from '@remixicons/vue/line'
import DemandeHeader from '~/components/demande/DemandeHeader.vue'
import EventInfoCards from '~/components/evenement/EventInfoCards.vue'

defineProps<{
  prestataireName: string
  prestataireImage: string
  slug: string
}>()

const emit = defineEmits<{
  continue: []
}>()

const { localEvent } = useLocalEvent()

const infoCards = useTemplateRef<InstanceType<typeof EventInfoCards>>('infoCards')

function onContinue() {
  if (infoCards.value?.validate()) emit('continue')
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

    .header {
      margin-bottom: $spacing-l;
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
