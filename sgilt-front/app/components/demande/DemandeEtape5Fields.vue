<template>
  <div class="fields">
    <div class="field-group">
      <label class="field-label">{{ $t('tunnel.etape5.field-city') }}</label>
      <input
        v-model="state.ville"
        class="field-input"
        :class="{ 'field-input--error': props.villeError }"
        type="text"
        name="city"
        autocomplete="address-level2"
        :placeholder="$t('tunnel.etape5.city-placeholder')"
        @focus="onFocus"
      />
      <p v-if="props.villeError" class="field-error">{{ props.villeError }}</p>
    </div>

    <div class="field-group">
      <label class="field-label">{{ $t('tunnel.etape5.field-venue') }}</label>
      <input
        v-model="state.lieu"
        class="field-input"
        type="text"
        name="venue"
        autocomplete="on"
        @focus="onFocus"
      />
    </div>

    <div class="field-group">
      <label class="field-label">{{ $t('tunnel.etape5.field-guests') }}</label>
      <input
        v-model="state.nbInvites"
        class="field-input"
        type="text"
        name="guest-count"
        autocomplete="on"
        :placeholder="$t('tunnel.etape5.guests-placeholder')"
        @focus="onFocus"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { DemandeState } from '~/types/demande'
import { scrollInputIntoView } from '~/utils/scrollInputIntoView'

const props = defineProps<{
  state: DemandeState
  mobile?: boolean
  villeError?: string | null
}>()

function onFocus(e: FocusEvent) {
  if (props.mobile) {
    scrollInputIntoView(e.target as HTMLElement)
  }
}
</script>

<style scoped lang="scss">
.fields {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: $spacing-xxs;
}

.field-label {
  font-size: 0.85rem;
  font-weight: 600;
  color: $text-primary;
}

.field-input {
  width: 100%;
  padding: $spacing-s $spacing-m;
  border: 1.5px solid $divider-color;
  border-radius: $radius-md;
  font-size: 0.95rem;
  font-family: inherit;
  color: $text-primary;
  background: #fff;
  outline: none;
  transition: border-color 180ms ease;
  box-sizing: border-box;

  &:focus {
    border-color: $brand-accent;
  }

  &--error {
    border-color: $state-error;
  }

  &::placeholder {
    color: $text-secondary;
    opacity: 0.5;
  }
}

.field-error {
  font-size: 0.8rem;
  color: $state-error;
  margin: 0;
}
</style>
