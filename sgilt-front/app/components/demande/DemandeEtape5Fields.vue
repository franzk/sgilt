<template>
  <div class="fields">
    <div class="field-group">
      <label class="field-label">📅 Date de l'événement</label>
      <SgiltDatePicker v-model="state.date" placeholder="Choisir une date" disabled />
    </div>

    <div class="field-group">
      <label class="field-label">📍 Ville</label>
      <input
        v-model="state.ville"
        class="field-input"
        type="text"
        placeholder="Ex : Strasbourg"
        @focus="onFocus"
      />
    </div>

    <div class="field-group">
      <label class="field-label">👥 Nombre d'invités</label>
      <input
        v-model="state.nbInvites"
        class="field-input"
        type="text"
        placeholder="Ex : Environ 40"
        @focus="onFocus"
      />
    </div>

    <div class="field-group field-group--row">
      <label class="field-label">📍 Lieu défini&nbsp;?</label>
      <div class="toggle-group">
        <button
          class="toggle-btn"
          :class="{ 'toggle-btn--active': state.lieuDefini }"
          type="button"
          @click="state.lieuDefini = true"
        >
          Oui
        </button>
        <button
          class="toggle-btn"
          :class="{ 'toggle-btn--active': !state.lieuDefini }"
          type="button"
          @click="state.lieuDefini = false"
        >
          Non
        </button>
      </div>
      <span v-if="!state.lieuDefini" class="lieu-non-defini">Lieu pas encore défini</span>
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
