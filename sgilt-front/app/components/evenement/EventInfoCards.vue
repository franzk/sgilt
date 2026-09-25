<template>
  <div class="event-info-cards">
    <div
      v-for="card in cards"
      :key="card.key"
      :ref="(el: HTMLElement) => setCardEl(card.key, el)"
      class="card"
      :class="{ 'has-error': showErrors && card.missing }"
    >
      <span class="card-icon" aria-hidden="true">
        <component :is="card.icon" class="icon" />
      </span>
      <span class="card-text">
        <span class="card-label">{{ card.label }}</span>
        <span v-if="card.value" class="card-value">{{ card.value }}</span>
        <span v-else-if="showErrors && card.missing" class="card-error">
          {{ $t('event-info.required') }}
        </span>
      </span>
      <button v-if="card.editable" class="card-btn" type="button" @click="openField(card.key)">
        <PencilIcon v-if="card.value" class="icon" aria-hidden="true" />
        <AddIcon v-else class="icon" aria-hidden="true" />
        {{ card.value ? $t('event-info.edit') : $t('event-info.add') }}
      </button>
    </div>

    <!-- ── Panneau d'édition ─────────────────────────────────────────────────── -->
    <!-- Le panneau travaille sur un brouillon : fermer sans valider ne change rien. -->
    <SgiltBottomSheet v-model:open="sheetOpen" :title="activeCard?.label ?? ''" overlay>
      <div v-if="activeField" class="sheet-body">
        <!-- Les choix par cartes valident au clic, comme dans le tunnel /organisation. -->
        <OrganisationChoiceStep
          v-if="activeField === 'eventType'"
          v-model="draft.eventType"
          v-model:autre-value="draft.eventTypeAutre"
          :options="EVENT_TYPE_OPTIONS"
          :autre-placeholder="$t('event-info.event-type-autre-placeholder')"
          @next="validateField"
        />
        <OrganisationChoiceStep
          v-else-if="activeField === 'ambiance'"
          v-model="draft.ambiance"
          v-model:autre-value="draft.ambianceAutre"
          :options="AMBIANCE_OPTIONS"
          :autre-placeholder="$t('organisation.steps.ambiance.autre-placeholder')"
          @next="validateField"
        />
        <OrganisationChoiceStep
          v-else-if="activeField === 'momentCle'"
          v-model="draft.momentCle"
          v-model:autre-value="draft.momentCleAutre"
          :options="MOMENT_CLE_OPTIONS"
          :autre-placeholder="$t('organisation.steps.moment-cle.autre-placeholder')"
          @next="validateField"
        />

        <template v-else>
          <template v-if="activeField === 'title'">
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
          <SgiltDatePicker
            v-else-if="activeField === 'date'"
            v-model="draft.date"
            inline
            fullwidth
          />
          <OrganisationLieuStep
            v-else-if="activeField === 'lieu'"
            v-model:ville="draft.ville"
            v-model:lieu="draft.lieu"
            :city-required="essentialsRequired"
          />
          <OrganisationInvitesStep
            v-else-if="activeField === 'nbInvites'"
            v-model="draft.nbInvites"
          />
          <OrganisationPetitPlusStep v-else v-model="draft.description" />

          <button class="validate" type="button" @click="validateField">
            {{ $t('event-info.validate') }}
          </button>
        </template>
      </div>
    </SgiltBottomSheet>
  </div>
</template>

<script setup lang="ts">
import { markRaw, type Component, type ComponentPublicInstance } from 'vue'
import {
  AddIcon,
  CalendarEventIcon,
  Edit2Icon,
  FileTextIcon,
  GroupIcon,
  MapPin2Icon,
  Music2Icon,
  PencilIcon,
  Sparkling2Icon,
  SparklingIcon,
} from '@remixicons/vue/line'
import SgiltBottomSheet from '~/components/basics/sheets/SgiltBottomSheet.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import { choiceLabel, type EventInfoFields } from '~/composables/useLocalEvent'
import { AMBIANCE_OPTIONS, EVENT_TYPE_OPTIONS, MOMENT_CLE_OPTIONS } from '~/types/demande'
import { formatDateWithWeekday } from '~/utils/dateUtils'
import { EVENT_TYPE_CATALOG } from '~/utils/eventTypes'

const props = defineProps<{
  // Informations affichées. Le composant ne les modifie pas : chaque validation de panneau
  // émet `update`, au parent de l'appliquer (à l'événement ou à un brouillon).
  event: EventInfoFields
  // Ajoute la carte « Nom de l'événement » en tête.
  withTitle?: boolean
  // Date, type et ville obligatoires (envoi d'une demande) : voir validate().
  essentialsRequired?: boolean
}>()

const emit = defineEmits<{
  update: [patch: EventInfoFields]
}>()

const { t } = useI18n()

// ── Cartes ────────────────────────────────────────────────────────────────────

type FieldKey =
  | 'title'
  | 'eventType'
  | 'date'
  | 'lieu'
  | 'nbInvites'
  | 'ambiance'
  | 'momentCle'
  | 'description'

interface InfoCard {
  key: FieldKey
  label: string
  icon: Component
  value: string | null
  // Obligatoire et non renseigné : fait échouer validate().
  missing: boolean
  editable: boolean
}

const eventTypeIcon = computed(
  () =>
    EVENT_TYPE_CATALOG.find((type) => type.key === props.event.eventType)?.icon ??
    markRaw(SparklingIcon),
)

const cards = computed<InfoCard[]>(() => {
  const event = props.event
  const required = props.essentialsRequired
  const infoCards: InfoCard[] = [
    {
      key: 'eventType',
      label: t('event-info.fields.event-type'),
      icon: eventTypeIcon.value,
      value: choiceLabel(EVENT_TYPE_OPTIONS, event.eventType, event.eventTypeAutre),
      missing: required && !event.eventType,
      // Le type n'est plus modifiable une fois choisi : en changer revient à repartir de zéro.
      editable: !event.eventType,
    },
    {
      key: 'date',
      label: t('event-info.fields.date'),
      icon: markRaw(CalendarEventIcon),
      value: formatDateWithWeekday(event.date) || null,
      missing: required && !event.date,
      editable: true,
    },
    {
      key: 'lieu',
      label: t('event-info.fields.lieu'),
      icon: markRaw(MapPin2Icon),
      value: [event.lieu, event.ville].filter(Boolean).join(', ') || null,
      missing: required && !event.ville,
      editable: true,
    },
    {
      key: 'nbInvites',
      label: t('event-info.fields.nb-invites'),
      icon: markRaw(GroupIcon),
      value: event.nbInvites ? t('event-info.nb-invites-value', { value: event.nbInvites }) : null,
      missing: false,
      editable: true,
    },
    {
      key: 'ambiance',
      label: t('event-info.fields.ambiance'),
      icon: markRaw(Sparkling2Icon),
      value: choiceLabel(AMBIANCE_OPTIONS, event.ambiance, event.ambianceAutre),
      missing: false,
      editable: true,
    },
    {
      key: 'momentCle',
      label: t('event-info.fields.moment-cle'),
      icon: markRaw(Music2Icon),
      value: choiceLabel(MOMENT_CLE_OPTIONS, event.momentCle, event.momentCleAutre),
      missing: false,
      editable: true,
    },
    {
      key: 'description',
      label: t('event-info.fields.description'),
      icon: markRaw(FileTextIcon),
      value: event.description || null,
      missing: false,
      editable: true,
    },
  ]
  if (!props.withTitle) return infoCards
  return [
    {
      key: 'title',
      label: t('event-info.fields.title'),
      icon: markRaw(Edit2Icon),
      value: event.title || null,
      missing: false,
      editable: true,
    },
    ...infoCards,
  ]
})

// ── Panneau d'édition ─────────────────────────────────────────────────────────

const sheetOpen = ref(false)
const activeField = ref<FieldKey | null>(null)
const activeCard = computed(() => cards.value.find((card) => card.key === activeField.value))
const titleError = ref(false)

// Copie des informations, réinitialisée à chaque ouverture du panneau.
function eventSnapshot(): EventInfoFields {
  const event = props.event
  return {
    title: event.title,
    eventType: event.eventType,
    eventTypeAutre: event.eventTypeAutre,
    date: event.date,
    ville: event.ville,
    lieu: event.lieu,
    nbInvites: event.nbInvites,
    ambiance: event.ambiance,
    ambianceAutre: event.ambianceAutre,
    momentCle: event.momentCle,
    momentCleAutre: event.momentCleAutre,
    description: event.description,
  }
}

const draft = reactive<EventInfoFields>(eventSnapshot())

function openField(key: FieldKey) {
  Object.assign(draft, eventSnapshot())
  titleError.value = false
  activeField.value = key
  sheetOpen.value = true
}

function validateField() {
  // Le nom ne peut pas être vidé : l'erreur reste dans le panneau.
  if (activeField.value === 'title') {
    draft.title = draft.title.trim()
    if (!draft.title) {
      titleError.value = true
      return
    }
  }
  emit('update', { ...draft })
  sheetOpen.value = false
}

// ── Validation ────────────────────────────────────────────────────────────────

const showErrors = ref(false)
const cardEls = new Map<FieldKey, HTMLElement>()

function setCardEl(key: FieldKey, el: Element | ComponentPublicInstance | null) {
  if (el instanceof HTMLElement) cardEls.set(key, el)
}

// Signale les informations obligatoires manquantes et fait défiler jusqu'à la première.
// Retourne vrai si tout est renseigné.
function validate(): boolean {
  showErrors.value = true
  const firstMissing = cards.value.find((card) => card.missing)
  if (!firstMissing) return true
  cardEls.get(firstMissing.key)?.scrollIntoView({ behavior: 'smooth', block: 'center' })
  return false
}

defineExpose({ validate })
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.event-info-cards {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;

  .card {
    display: flex;
    align-items: center;
    gap: $spacing-s;
    padding: $spacing-s $spacing-m;
    border: 1.5px solid $divider-color;
    border-radius: $radius-lg;
    background: $surface-white;
    transition: border-color 180ms ease;

    &.has-error {
      border-color: $state-error;
    }

    .card-icon {
      display: flex;
      flex-shrink: 0;
      align-items: center;
      justify-content: center;
      width: 2.75rem;
      height: 2.75rem;
      border-radius: 50%;
      background: rgba($brand-accent, 0.18);
      color: $text-primary;

      .icon {
        width: 1.375rem;
        height: 1.375rem;
      }
    }

    .card-text {
      display: flex;
      flex: 1;
      flex-direction: column;
      gap: 0.125rem;
      min-width: 0;

      .card-label {
        color: $text-secondary;
        font-size: $font-size-xs;
      }

      .card-value {
        color: $text-primary;
        font-size: $font-size-sm;
        font-weight: $font-weight-semibold;
        line-height: $line-height-normal;
        overflow-wrap: anywhere;
      }

      .card-error {
        color: $state-error;
        font-size: $font-size-xs;
      }
    }

    .card-btn {
      display: inline-flex;
      flex-shrink: 0;
      align-items: center;
      gap: $spacing-xxs;
      padding: $spacing-xs $spacing-s;
      border: none;
      border-radius: 9999px;
      background: $surface-soft;
      color: $text-primary;
      font-family: inherit;
      font-size: $font-size-xs;
      cursor: pointer;

      .icon {
        width: 1rem;
        height: 1rem;
      }
    }
  }
}

// Hors de .event-info-cards : le panneau est téléporté hors de ce conteneur par le drawer.
.sheet-body {
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
