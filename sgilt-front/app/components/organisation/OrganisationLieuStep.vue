<template>
  <div class="lieu-step">
    <label class="field-group">
      <span class="field-label">
        {{ $t('organisation.steps.lieu.field-city') }}
        <span class="optional">{{ $t('organisation.steps.lieu.optional') }}</span>
      </span>
      <span class="field">
        <MapPin2Icon class="icon" aria-hidden="true" />
        <input
          v-model="state.ville"
          class="input"
          type="text"
          name="city"
          autocomplete="address-level2"
          :placeholder="$t('organisation.steps.lieu.city-placeholder')"
        />
      </span>
    </label>

    <label class="field-group">
      <span class="field-label">
        {{ $t('organisation.steps.lieu.field-venue') }}
        <span class="optional">{{ $t('organisation.steps.lieu.optional') }}</span>
      </span>
      <span class="field">
        <MapPin2Icon class="icon" aria-hidden="true" />
        <input
          v-model="state.lieu"
          class="input"
          type="text"
          name="venue"
          :placeholder="$t('organisation.steps.lieu.venue-placeholder')"
        />
      </span>
    </label>
  </div>
</template>

<script setup lang="ts">
import { MapPin2Icon } from '@remixicons/vue/line'
import { useDemande } from '~/composables/useDemande'

const { state } = useDemande()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.lieu-step {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;

  .field-group {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;

    .field-label {
      color: $text-primary;
      font-size: $font-size-sm;
      font-weight: $font-weight-semibold;

      .optional {
        color: $text-secondary;
        font-weight: $font-weight-regular;
      }
    }

    .field {
      display: flex;
      align-items: center;
      gap: $spacing-s;
      height: 3.25rem;
      padding: 0 $spacing-m;
      border: 1.5px solid $divider-color;
      border-radius: $radius-lg;
      background: $surface-white;
      transition: border-color 180ms ease;

      &:focus-within {
        border-color: $brand-accent;
      }

      .icon {
        flex-shrink: 0;
        width: 1.25rem;
        height: 1.25rem;
        color: $text-primary;
      }

      .input {
        flex: 1;
        min-width: 0;
        height: 100%;
        border: none;
        background: transparent;
        color: $text-primary;
        font-family: inherit;
        font-size: $font-size-sm;
        outline: none;

        &::placeholder {
          color: $text-secondary;
          opacity: 0.6;
        }
      }
    }
  }
}
</style>
