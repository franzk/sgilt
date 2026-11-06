<!--
  Éditeur d'une information de l'événement (une carte de EventInfoCards) : choix par cartes,
  nom, lieu, horaires (début et fin, facultatifs) ou description. Travaille sur son propre
  brouillon ; la validation émet les informations, au parent de les appliquer.
-->
<template>
  <div class="field-editor">
    <!-- Les choix par cartes valident au clic, comme dans le tunnel /organisation. -->
    <OrganisationChoiceStep
      v-if="field === 'eventType'"
      v-model="draft.eventType"
      :options="EVENT_TYPE_OPTIONS"
      :autre-placeholder="$t('event-info.event-type-autre-placeholder')"
      @next="validate"
    />
    <OrganisationChoiceStep
      v-else-if="field === 'ambiance'"
      v-model="draft.ambiance"
      :options="AMBIANCE_OPTIONS"
      :autre-placeholder="$t('organisation.steps.ambiance.autre-placeholder')"
      @next="validate"
    />
    <OrganisationChoiceStep
      v-else-if="field === 'momentCle'"
      v-model="draft.momentCle"
      :options="MOMENT_CLE_OPTIONS"
      :autre-placeholder="$t('organisation.steps.moment-cle.autre-placeholder')"
      @next="validate"
    />
    <OrganisationInvitesStep
      v-else-if="field === 'nbInvites'"
      v-model="draft.nbInvites"
      @next="validate"
    />

    <template v-else>
      <template v-if="field === 'title'">
        <input
          v-model="draft.title"
          class="title-input"
          :class="{ invalid: titleError }"
          type="text"
          :aria-label="$t('event-info.fields.title')"
          :placeholder="$t('event-info.title-placeholder')"
          @input="titleError = false"
        />
        <p v-if="titleError" class="error">{{ $t('event-info.title-required') }}</p>
      </template>
      <div v-else-if="field === 'horaires'" class="hours">
        <label class="hour">
          <span class="hour-label">{{ $t('event-info.hours.start') }}</span>
          <input v-model="draft.startTime" class="hour-input" type="time" />
        </label>
        <label class="hour">
          <span class="hour-label">{{ $t('event-info.hours.end') }}</span>
          <input v-model="draft.endTime" class="hour-input" type="time" />
        </label>
        <p v-if="endsNextDay" class="hint">{{ $t('event-info.hours.next-day') }}</p>
      </div>
      <OrganisationLieuStep
        v-else-if="field === 'lieu'"
        v-model:ville="draft.ville"
        v-model:lieu="draft.lieu"
        :city-required="essentialsRequired && ESSENTIAL_EVENT_INFO.includes('lieu')"
      />
      <OrganisationPetitPlusStep v-else v-model="draft.description" />

      <p v-if="essentialError" class="error">{{ $t('event-info.essential-required') }}</p>
      <button class="validate" type="button" @click="validate">
        {{ $t('event-info.validate') }}
      </button>
    </template>
  </div>
</template>

<script setup lang="ts">
// Éditeur d'une information de l'événement (une carte de EventInfoCards). Il travaille sur son
// propre brouillon, copié à l'ouverture : fermer sans valider ne change rien. La validation émet
// les informations, au parent de les appliquer.
import {
  ESSENTIAL_EVENT_INFO,
  isEssentialInfoMissing,
  type EventInfoFieldKey,
  type EventInfoFields,
} from '~/composables/useLocalEvent'
import { AMBIANCE_OPTIONS, EVENT_TYPE_OPTIONS, MOMENT_CLE_OPTIONS } from '~/types/evenement'

const props = defineProps<{
  field: EventInfoFieldKey
  event: EventInfoFields
  // Informations essentielles obligatoires (envoi d'une demande, voir ESSENTIAL_EVENT_INFO).
  essentialsRequired?: boolean
}>()

const emit = defineEmits<{
  validate: [fields: EventInfoFields]
}>()

// Brouillon : copie des seules informations modifiables, faite à l'ouverture.
const draft = reactive<EventInfoFields>({
  title: props.event.title,
  eventType: props.event.eventType,
  date: props.event.date,
  ville: props.event.ville,
  lieu: props.event.lieu,
  nbInvites: props.event.nbInvites,
  ambiance: props.event.ambiance,
  momentCle: props.event.momentCle,
  description: props.event.description,
  startTime: props.event.startTime ?? '',
  endTime: props.event.endTime ?? '',
})

// Fin avant (ou à) l'heure du début : elle tombe le lendemain ('HH:mm' se compare comme du texte).
const endsNextDay = computed(() => !!draft.startTime && !!draft.endTime && draft.endTime <= draft.startTime)
const titleError = ref(false)
const essentialError = ref(false)

function validate() {
  // Une information essentielle exigée ne peut pas être laissée vide (ex. la ville d'un événement
  // enregistré).
  essentialError.value = props.essentialsRequired && isEssentialInfoMissing(draft, props.field)
  if (essentialError.value) return

  // Le nom ne peut pas être vidé : l'erreur reste dans l'éditeur.
  if (props.field === 'title') {
    draft.title = draft.title.trim()
    if (!draft.title) {
      titleError.value = true
      return
    }
  }
  emit('validate', { ...draft })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.field-editor {
  display: flex;
  flex-direction: column;
  padding: 0 $spacing-m $spacing-l;

  .title-input {
    height: 3.25rem;
    padding: 0 $spacing-m;
    border: 1.5px solid $divider-color;
    border-radius: $radius-lg;
    background: $surface-white;
    color: $brand-primary;
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.25rem;
    font-weight: 600;
    outline: none;
    transition: border-color 180ms ease;

    &:focus {
      border-color: $brand-accent;
    }

    &.invalid {
      border-color: $state-error;
    }

    &::placeholder {
      color: $text-secondary;
      opacity: 0.6;
    }
  }

  .hours {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;

    .hour {
      display: flex;
      flex-direction: column;
      gap: 0.3rem;

      .hour-label {
        font-size: $font-size-sm;
        font-weight: 600;
        color: $text-primary;
      }

      .hour-input {
        height: 3.25rem;
        padding: 0 $spacing-m;
        border: 1.5px solid $divider-color;
        border-radius: $radius-lg;
        background: $surface-white;
        color: $text-primary;
        font-family: inherit;
        font-size: 1rem;
        outline: none;
        transition: border-color 180ms ease;

        &:focus {
          border-color: $brand-accent;
        }
      }
    }

    .hint {
      margin: 0;
      color: $text-secondary;
      font-size: $font-size-xs;
    }
  }

  .error {
    margin: $spacing-xs 0 0;
    color: $state-error;
    font-size: $font-size-xs;
  }

  // Même bouton que le CTA du tunnel /organisation.
  .validate {
    display: inline-flex;
    align-items: center;
    justify-content: center;
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
  }
}
</style>
