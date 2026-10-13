<template>
  <div class="event-info-cards">
    <div v-for="card in cards" :key="card.key" class="card-group">
      <div
        :ref="(el: HTMLElement) => setCardEl(card.key, el)"
        class="card"
        :class="{
          'has-error': showErrors && isMissing(card.key),
          open: isDesktop && isOpen(card.key),
        }"
      >
        <span class="card-icon" aria-hidden="true">
          <component :is="card.icon" class="icon" />
        </span>
        <span class="card-text">
          <span class="card-label">{{ card.label }}</span>
          <!-- Date modifiable : choisie directement dans la carte, appliquée aussitôt. -->
          <SgiltDatePicker
            v-if="isDateInCard(card)"
            v-model="eventDate"
            class="card-date"
            :placeholder="$t('event-info.date-placeholder')"
            :error="showErrors && isMissing(card.key)"
          />
          <template v-else>
            <span v-if="card.value" class="card-value">{{ card.value }}</span>
            <span v-else-if="showErrors && isMissing(card.key)" class="card-error">
              {{ $t('event-info.required') }}
            </span>
          </template>
        </span>
        <button
          v-if="card.editable && !isDateInCard(card)"
          class="card-btn"
          type="button"
          @click="toggleField(card.key)"
        >
          <template v-if="isOpen(card.key)">{{ $t('common.cancel') }}</template>
          <template v-else>
            <PencilIcon v-if="card.value" class="icon" aria-hidden="true" />
            <AddIcon v-else class="icon" aria-hidden="true" />
            {{ card.value ? $t('event-info.edit') : $t('event-info.add') }}
          </template>
        </button>
      </div>

      <!-- Éditeur de la carte : déroulé sous elle sur desktop, en bottom sheet sur mobile. -->
      <SgiltInlinePanel
        v-if="card.editable && !isDateInCard(card)"
        :open="isOpen(card.key)"
        :title="card.label"
        @update:open="(open) => !open && closeField()"
      >
        <EventInfoFieldEditor
          :field="card.key"
          :event="event"
          :essentials-required="essentialsRequired"
          @validate="onValidated"
        />
      </SgiltInlinePanel>
    </div>
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
import SgiltInlinePanel from '~/components/basics/sheets/SgiltInlinePanel.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import EventInfoFieldEditor from '~/components/evenement/EventInfoFieldEditor.vue'
import {
  choiceLabel,
  isEssentialInfoMissing,
  type EventInfoFieldKey,
  type EventInfoFields,
} from '~/composables/useLocalEvent'
import { AMBIANCE_OPTIONS, EVENT_TYPE_OPTIONS, MOMENT_CLE_OPTIONS } from '~/types/evenement'
import { formatDateWithWeekday } from '~/utils/dateUtils'
import { EVENT_TYPE_CATALOG } from '~/utils/eventTypes'

const props = defineProps<{
  // Informations affichées. Le composant ne les modifie pas : chaque validation de panneau
  // émet `update`, au parent de l'appliquer (à l'événement ou à un brouillon).
  event: EventInfoFields
  // Ajoute la carte « Nom de l'événement » en tête.
  withTitle?: boolean
  // Informations essentielles obligatoires (envoi d'une demande, voir ESSENTIAL_EVENT_INFO) :
  // voir validate().
  essentialsRequired?: boolean
  // Date non modifiable (événement en base : en changer demande l'accord des prestataires).
  dateLocked?: boolean
}>()

const emit = defineEmits<{
  update: [patch: Partial<EventInfoFields>]
}>()

const { t } = useI18n()

// ── Cartes ────────────────────────────────────────────────────────────────────

interface InfoCard {
  key: EventInfoFieldKey
  label: string
  icon: Component
  value: string | null
  editable: boolean
}

const eventTypeIcon = computed(
  () =>
    EVENT_TYPE_CATALOG.find((type) => type.key === props.event.eventType)?.icon ??
    markRaw(SparklingIcon),
)

const cards = computed<InfoCard[]>(() => {
  const event = props.event
  const infoCards: InfoCard[] = [
    {
      key: 'eventType',
      label: t('event-info.fields.event-type'),
      icon: eventTypeIcon.value,
      value: choiceLabel(EVENT_TYPE_OPTIONS, event.eventType),
      // Le type n'est plus modifiable une fois choisi : en changer revient à repartir de zéro.
      editable: !event.eventType,
    },
    {
      key: 'date',
      label: t('event-info.fields.date'),
      icon: markRaw(CalendarEventIcon),
      value: formatDateWithWeekday(event.date) || null,
      editable: !props.dateLocked,
    },
    {
      key: 'lieu',
      label: t('event-info.fields.lieu'),
      icon: markRaw(MapPin2Icon),
      value: [event.lieu, event.ville].filter(Boolean).join(', ') || null,
      editable: true,
    },
    {
      key: 'nbInvites',
      label: t('event-info.fields.nb-invites'),
      icon: markRaw(GroupIcon),
      value: event.nbInvites ? t('event-info.nb-invites-value', { value: event.nbInvites }) : null,
      editable: true,
    },
    {
      key: 'ambiance',
      label: t('event-info.fields.ambiance'),
      icon: markRaw(Sparkling2Icon),
      value: choiceLabel(AMBIANCE_OPTIONS, event.ambiance),
      editable: true,
    },
    {
      key: 'momentCle',
      label: t('event-info.fields.moment-cle'),
      icon: markRaw(Music2Icon),
      value: choiceLabel(MOMENT_CLE_OPTIONS, event.momentCle),
      editable: true,
    },
    {
      key: 'description',
      label: t('event-info.fields.description'),
      icon: markRaw(FileTextIcon),
      value: event.description || null,
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
      editable: true,
    },
    ...infoCards,
  ]
})

// Information essentielle exigée (envoi d'une demande) et non renseignée : fait échouer validate().
function isMissing(key: EventInfoFieldKey): boolean {
  return props.essentialsRequired && isEssentialInfoMissing(props.event, key)
}

// ── Panneau d'édition ─────────────────────────────────────────────────────────

// La date se choisit dans sa carte, sans éditeur (quand elle est modifiable).
function isDateInCard(card: InfoCard): boolean {
  return card.key === 'date' && card.editable
}

// Date choisie dans sa carte : appliquée aussitôt. L'effacement du sélecteur renvoie null : pas
// de date.
const eventDate = computed({
  get: () => props.event.date,
  set: (date: Date | null | undefined) => emit('update', { date: date ?? undefined }),
})

// Carte dont l'éditeur est ouvert (une seule à la fois). L'éditeur est recréé à chaque
// ouverture : il repart d'une copie fraîche des informations.
const openKey = ref<EventInfoFieldKey | null>(null)

const { isDesktop } = useDevice()

function isOpen(key: EventInfoFieldKey): boolean {
  return openKey.value === key
}

// Le bouton d'une carte ouvre son éditeur ; recliquer le referme sans valider.
function toggleField(key: EventInfoFieldKey) {
  openKey.value = isOpen(key) ? null : key
}

function closeField() {
  openKey.value = null
}

// Informations validées dans l'éditeur : transmises au parent, qui les applique.
function onValidated(fields: EventInfoFields) {
  emit('update', fields)
  closeField()
}

// ── Validation ────────────────────────────────────────────────────────────────

const showErrors = ref(false)
const cardEls = new Map<EventInfoFieldKey, HTMLElement>()

function setCardEl(key: EventInfoFieldKey, el: Element | ComponentPublicInstance | null) {
  if (el instanceof HTMLElement) cardEls.set(key, el)
}

// Signale les informations obligatoires manquantes et fait défiler jusqu'à la première.
// Retourne vrai si tout est renseigné.
function validate(): boolean {
  showErrors.value = true
  const firstMissing = cards.value.find((card) => isMissing(card.key))
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

  .card-group {
    display: flex;
    flex-direction: column;
  }

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

    // Éditeur déroulé dessous : la carte et l'éditeur forment un seul bloc.
    &.open {
      border-color: $brand-accent;
      border-bottom: none;
      border-radius: $radius-lg $radius-lg 0 0;
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

      .card-date {
        margin-top: $spacing-xxs;
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

  // ── Éditeur déroulé sous sa carte (desktop) : la carte et lui forment un seul bloc ──
  :deep(.sgilt-inline-panel) {
    padding-top: $spacing-s;
    border: 1.5px solid $brand-accent;
    border-top: none;
    border-radius: 0 0 $radius-lg $radius-lg;
    background: $surface-white;
  }
}
</style>
