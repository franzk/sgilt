<template>
  <div class="fields">
    <div class="field-group">
      <label class="field-label">{{ $t('tunnel.etape5.field-date') }}</label>
      <SgiltDatePicker v-model="state.date" :placeholder="$t('tunnel.etape5.date-placeholder')" disabled />
    </div>

    <div class="field-group">
      <label class="field-label">{{ $t('tunnel.etape5.field-city') }}</label>
      <input
        v-model="state.ville"
        class="field-input"
        type="text"
        :placeholder="$t('tunnel.etape5.city-placeholder')"
        @focus="onFocus"
      />
    </div>

    <div class="field-group">
      <label class="field-label">{{ $t('tunnel.etape5.field-guests') }}</label>
      <input
        v-model="state.nbInvites"
        class="field-input"
        type="text"
        :placeholder="$t('tunnel.etape5.guests-placeholder')"
        @focus="onFocus"
      />
    </div>

    <div class="field-group field-group--row">
      <label class="field-label">{{ $t('tunnel.etape5.field-venue') }}</label>
      <div class="toggle-group">
        <button
          class="toggle-btn"
          :class="{ 'toggle-btn--active': state.lieuDefini }"
          type="button"
          @click="state.lieuDefini = true"
        >
          {{ $t('tunnel.etape5.venue-yes') }}
        </button>
        <button
          class="toggle-btn"
          :class="{ 'toggle-btn--active': !state.lieuDefini }"
          type="button"
          @click="state.lieuDefini = false"
        >
          {{ $t('tunnel.etape5.venue-no') }}
        </button>
      </div>
      <span v-if="!state.lieuDefini" class="lieu-non-defini">{{ $t('tunnel.etape5.venue-undefined') }}</span>
    </div>

    <div v-if="state.lieuDefini" class="field-group">
      <input
        v-model="state.lieu"
        class="field-input"
        type="text"
        @focus="onFocus"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import type { DemandeState } from '~/types/demande'
import { scrollInputIntoView } from '~/utils/scrollInputIntoView'

const props = defineProps<{
  state: DemandeState
  mobile?: boolean
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

  &--row {
    flex-direction: row;
    align-items: center;
    flex-wrap: wrap;
    gap: $spacing-s;
  }

  button {
    height: 3rem;
  }
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

  &::placeholder {
    color: $text-secondary;
    opacity: 0.5;
  }
}

.toggle-group {
  display: flex;
  border: 1.5px solid $divider-color;
  border-radius: $radius-md;
  overflow: hidden;
}

.toggle-btn {
  padding: $spacing-xxs $spacing-m;
  border: none;
  background: #fff;
  font-size: 0.9rem;
  font-family: inherit;
  color: $text-secondary;
  cursor: pointer;
  transition:
    background 150ms ease,
    color 150ms ease;

  & + & {
    border-left: 1px solid $divider-color;
  }

  &--active {
    background: $brand-accent;
    color: #fff;
    font-weight: 600;
  }
}

.lieu-non-defini {
  font-size: 0.82rem;
  color: $text-secondary;
  font-style: italic;
}
</style>
