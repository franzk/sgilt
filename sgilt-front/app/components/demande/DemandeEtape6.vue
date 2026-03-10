<template>
  <div class="etape">
    <h2 class="etape__question">Comment vous contacter ?</h2>

    <!-- Coordonnées -->
    <div class="section-title">Vos coordonnées</div>
    <div class="fields">
      <div class="field-group">
        <label class="field-label">📧 Email <span class="required">*</span></label>
        <input
          v-model="state.email"
          class="field-input"
          type="email"
          placeholder="votre@email.fr"
          autocomplete="email"
        />
      </div>

      <div class="field-group">
        <label class="field-label">📱 Téléphone <span class="required">*</span></label>
        <input
          v-model="state.telephone"
          class="field-input"
          type="tel"
          placeholder="06 12 34 56 78"
          autocomplete="tel"
        />
      </div>

      <div class="field-group">
        <textarea
          v-model="state.prestataireMessage"
          class="description-textarea"
          placeholder="(optionnel) Votre message pour le prestataire"
          rows="6"
        />
      </div>

      <div class="field-group submit">
        <SgiltButton type="primary" :disabled="!formValid" @click="emit('change')"
          >Envoyer ma demande</SgiltButton
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
    />
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import type { DemandeState } from '~/types/demande'

const props = defineProps<{
  state: DemandeState
  eventTypeLabel: string | null
  eventTypeEmoji: string
  ambianceLabel: string | null
  ambianceEmoji: string
  momentCleLabel: string | null
  momentCleEmoji: string
}>()

const formValid = computed(() => {
  return !!props.state.email && !!props.state.telephone
})

const emit = defineEmits<{
  (e: 'change'): void
}>()
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

.description-textarea {
  width: 100%;
  padding: $spacing-m;
  border: 1.5px solid $divider-color;
  border-radius: $radius-md;
  font-size: 0.95rem;
  font-family: inherit;
  color: $text-primary;
  background: #fff;
  resize: vertical;
  outline: none;
  transition: border-color 180ms ease;
  box-sizing: border-box;
  line-height: 1.6;

  &:focus {
    border-color: $brand-accent;
  }

  &::placeholder {
    color: $text-secondary;
    opacity: 0.5;
  }
}

.submit {
  button {
    height: 3rem;
  }
}
</style>
