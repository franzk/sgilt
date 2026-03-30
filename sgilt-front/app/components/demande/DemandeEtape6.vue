<template>
  <div class="etape">
    <h2 class="question">{{ isNewEventFlow ? 'Votre message' : 'Comment vous contacter ?' }}</h2>

    <!-- Coordonnées (masquées en flow new-event, récupérées depuis le profil) -->
    <template v-if="!isNewEventFlow">
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
      </div>
    </template>

    <div class="fields">
      <div class="field-group">
        <textarea
          v-model="state.prestataireMessage"
          class="description-textarea"
          placeholder="(optionnel) Votre message pour le prestataire"
          rows="6"
        />
      </div>

      <div class="field-group submit">
        <SgiltButton type="primary" :disabled="!formValid" @click="submit"
          >Envoyer ma demande</SgiltButton
        >
      </div>
    </div>

    <!-- Récapitulatif complet -->
    <DemandeRecap v-if="showRecap" />
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { useDemande } from '~/composables/useDemande'

defineProps<{ showRecap?: boolean }>()

const {
  state,
  submit,
  eventTypeLabel,
  eventTypeEmoji,
  ambianceLabel,
  ambianceEmoji,
  momentCleLabel,
  momentCleEmoji,
} = useDemande()

const { currentFlow } = useFlow()
const isNewEventFlow = computed(() => currentFlow.value === 'new-event')

const formValid = computed(() =>
  isNewEventFlow.value ? true : !!state.email && !!state.telephone,
)
</script>

<style scoped lang="scss">
.etape {
  .question {
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
</style>
