<template>
  <div class="etape">
    <h2 class="etape__question">Votre événement en pratique</h2>

    <!-- Infos pratiques -->
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
        />
      </div>

      <div class="field-group">
        <label class="field-label">👥 Nombre d'invités</label>
        <input
          v-model="state.nbInvites"
          class="field-input"
          type="text"
          placeholder="Ex : Environ 40"
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
        <span v-if="!state.lieuDefini" class="lieu-non-defini">Lieu non défini</span>
      </div>
      <div v-if="state.lieuDefini" class="field-group">
        <input v-model="state.lieu" class="field-input" type="text" />
      </div>
      <div class="field-group">
        <SgiltButton type="primary" :disabled="!formValid" @click="emit('change')"
          >Continuer →</SgiltButton
        >
      </div>
    </div>

    <!-- Récapitulatif complet -->
    <DemandeRecap
      :event-type-label="eventTypeLabel"
      :event-type-emoji="eventTypeEmoji"
      :ambiance-label="ambianceLabel"
      :ambiance-emoji="ambianceEmoji"
      :moment-cle-label="momentCleLabel"
      :moment-cle-emoji="momentCleEmoji"
      :date="state.date"
      :ville="state.ville"
      :nb-invites="state.nbInvites"
      :lieu="state.lieu"
    />
  </div>
</template>

<script setup lang="ts">
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import type { DemandeState } from '~/types/demande'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

const props = defineProps<{
  state: DemandeState
  eventTypeLabel: string | null
  eventTypeEmoji: string
  ambianceLabel: string | null
  ambianceEmoji: string
  momentCleLabel: string | null
  momentCleEmoji: string
}>()

const emit = defineEmits<{
  (e: 'change'): void
}>()

const formValid = computed(() => {
  return (
    !!props.state.date &&
    !!props.state.ville.trim() &&
    !!props.state.nbInvites.trim() &&
    Number(props.state.nbInvites) > 0
  )
})
</script>

<style scoped lang="scss">
.etape {
  &__question {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.5rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0 0 $spacing-l;
    line-height: 1.3;
  }
}

.section-title {
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: $text-secondary;
  margin: $spacing-l 0 $spacing-m;
}

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

.required {
  color: $state-error;
  margin-left: 2px;
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
