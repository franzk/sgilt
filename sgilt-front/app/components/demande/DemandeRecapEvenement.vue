<template>
  <div class="recap-evenement">
    <div class="column">
      <!-- ── En-tête ─────────────────────────────────────────────────────────── -->
      <div class="top-row">
        <button class="back" type="button" @click="navigateTo(`/${slug}`)">
          <ArrowLeftIcon class="icon" aria-hidden="true" />
          {{ $t('tunnel.recap-evenement.back') }}
        </button>
        <span class="prestataire">
          <span class="prestataire-name">
            {{ $t('tunnel.recap-evenement.request-to', { name: prestataireName }) }}
          </span>
          <SgiltImage
            class="prestataire-avatar"
            :src="prestataireImage"
            :alt="prestataireName"
            width="96"
            height="96"
          />
        </span>
      </div>

      <h1 class="title">{{ $t('tunnel.recap-evenement.title') }}</h1>
      <p class="subtitle">
        {{ $t('tunnel.recap-evenement.subtitle', { name: prestataireName }) }}
      </p>

      <!-- ── Cartes ──────────────────────────────────────────────────────────── -->
      <div class="cards">
        <div
          v-for="card in cards"
          :key="card.key"
          :ref="(el) => setCardEl(card.key, el)"
          class="card"
          :class="{ 'has-error': submitAttempted && card.missing }"
        >
          <span class="card-icon" aria-hidden="true">
            <component :is="card.icon" class="icon" />
          </span>
          <span class="card-text">
            <span class="card-label">{{ card.label }}</span>
            <span v-if="card.value" class="card-value">{{ card.value }}</span>
            <span v-else-if="submitAttempted && card.missing" class="card-error">
              {{ $t('tunnel.recap-evenement.required') }}
            </span>
          </span>
          <button v-if="card.editable" class="card-btn" type="button" @click="openField(card.key)">
            <PencilIcon v-if="card.value" class="icon" aria-hidden="true" />
            <AddIcon v-else class="icon" aria-hidden="true" />
            {{ card.value ? $t('tunnel.recap-evenement.edit') : $t('tunnel.recap-evenement.add') }}
          </button>
        </div>
      </div>

      <!-- ── Conseil ─────────────────────────────────────────────────────────── -->
      <div class="tip">
        <LightbulbIcon class="icon" aria-hidden="true" />
        <span class="tip-text">
          <strong class="tip-title">{{ $t('tunnel.recap-evenement.tip-title') }}</strong>
          {{ $t('tunnel.recap-evenement.tip-text') }}
        </span>
      </div>

      <button class="cta" type="button" @click="onContinue">
        {{ $t('tunnel.recap-evenement.continue') }}
        <ArrowRightIcon class="icon" aria-hidden="true" />
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
          :autre-placeholder="$t('tunnel.recap-evenement.event-type-autre-placeholder')"
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
          <SgiltDatePicker v-if="activeField === 'date'" v-model="draft.date" inline fullwidth />
          <OrganisationLieuStep
            v-else-if="activeField === 'lieu'"
            city-required
            v-model:ville="draft.ville"
            v-model:lieu="draft.lieu"
          />
          <OrganisationInvitesStep
            v-else-if="activeField === 'nbInvites'"
            v-model="draft.nbInvites"
          />
          <OrganisationPetitPlusStep v-else v-model="draft.description" />

          <button class="cta" type="button" @click="validateField">
            {{ $t('tunnel.recap-evenement.validate') }}
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
  ArrowLeftIcon,
  ArrowRightIcon,
  CalendarEventIcon,
  FileTextIcon,
  GroupIcon,
  LightbulbIcon,
  MapPin2Icon,
  Music2Icon,
  PencilIcon,
  Sparkling2Icon,
  SparklingIcon,
} from '@remixicons/vue/line'
import SgiltBottomSheet from '~/components/basics/sheets/SgiltBottomSheet.vue'
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import SgiltDatePicker from '~/components/basics/inputs/SgiltDatePicker.vue'
import { AMBIANCE_OPTIONS, EVENT_TYPE_OPTIONS, MOMENT_CLE_OPTIONS } from '~/types/demande'
import { formatDateWithWeekday } from '~/utils/dateUtils'
import { EVENT_TYPE_CATALOG } from '~/utils/eventTypes'

defineProps<{
  prestataireName: string
  prestataireImage: string
  slug: string
}>()

const { t } = useI18n()
const { localEvent, eventTypeLabel, ambianceLabel, momentCleLabel } = useLocalEvent()

// ── Cartes ────────────────────────────────────────────────────────────────────

type FieldKey =
  | 'eventType'
  | 'date'
  | 'lieu'
  | 'nbInvites'
  | 'ambiance'
  | 'momentCle'
  | 'description'

interface RecapCard {
  key: FieldKey
  label: string
  icon: Component
  value: string | null
  // Obligatoire et non renseigné : bloque « Continuer ».
  missing: boolean
  editable: boolean
}

const eventTypeIcon = computed(
  () =>
    EVENT_TYPE_CATALOG.find((type) => type.key === localEvent.eventType)?.icon ??
    markRaw(SparklingIcon),
)

const lieuValue = computed(
  () => [localEvent.lieu, localEvent.ville].filter(Boolean).join(', ') || null,
)

const cards = computed<RecapCard[]>(() => [
  {
    key: 'eventType',
    label: t('tunnel.recap-evenement.fields.event-type'),
    icon: eventTypeIcon.value,
    value: eventTypeLabel.value,
    missing: !localEvent.eventType,
    // Le type n'est plus modifiable une fois choisi : en changer revient à repartir de zéro.
    editable: !localEvent.eventType,
  },
  {
    key: 'date',
    label: t('tunnel.recap-evenement.fields.date'),
    icon: markRaw(CalendarEventIcon),
    value: formatDateWithWeekday(localEvent.date) || null,
    missing: !localEvent.date,
    editable: true,
  },
  {
    key: 'lieu',
    label: t('tunnel.recap-evenement.fields.lieu'),
    icon: markRaw(MapPin2Icon),
    value: lieuValue.value,
    missing: !localEvent.ville,
    editable: true,
  },
  {
    key: 'nbInvites',
    label: t('tunnel.recap-evenement.fields.nb-invites'),
    icon: markRaw(GroupIcon),
    value: localEvent.nbInvites
      ? t('tunnel.recap-evenement.nb-invites-value', { value: localEvent.nbInvites })
      : null,
    missing: false,
    editable: true,
  },
  {
    key: 'ambiance',
    label: t('tunnel.recap-evenement.fields.ambiance'),
    icon: markRaw(Sparkling2Icon),
    value: ambianceLabel.value,
    missing: false,
    editable: true,
  },
  {
    key: 'momentCle',
    label: t('tunnel.recap-evenement.fields.moment-cle'),
    icon: markRaw(Music2Icon),
    value: momentCleLabel.value,
    missing: false,
    editable: true,
  },
  {
    key: 'description',
    label: t('tunnel.recap-evenement.fields.description'),
    icon: markRaw(FileTextIcon),
    value: localEvent.description || null,
    missing: false,
    editable: true,
  },
])

// ── Panneau d'édition ─────────────────────────────────────────────────────────

const sheetOpen = ref(false)
const activeField = ref<FieldKey | null>(null)
const activeCard = computed(() => cards.value.find((card) => card.key === activeField.value))

// Brouillon réinitialisé depuis l'événement à chaque ouverture ; « Valider » le recopie dans
// l'événement (le récap est une vue de l'événement, pas une copie propre à la demande).
function eventSnapshot() {
  return {
    eventType: localEvent.eventType,
    eventTypeAutre: localEvent.eventTypeAutre,
    date: localEvent.date,
    ville: localEvent.ville,
    lieu: localEvent.lieu,
    nbInvites: localEvent.nbInvites,
    ambiance: localEvent.ambiance,
    ambianceAutre: localEvent.ambianceAutre,
    momentCle: localEvent.momentCle,
    momentCleAutre: localEvent.momentCleAutre,
    description: localEvent.description,
  }
}

const draft = reactive(eventSnapshot())

function openField(key: FieldKey) {
  Object.assign(draft, eventSnapshot())
  activeField.value = key
  sheetOpen.value = true
}

function validateField() {
  Object.assign(localEvent, draft)
  sheetOpen.value = false
}

// ── Continuer ─────────────────────────────────────────────────────────────────

const submitAttempted = ref(false)
const cardEls = new Map<FieldKey, HTMLElement>()

function setCardEl(key: FieldKey, el: Element | ComponentPublicInstance | null) {
  if (el instanceof HTMLElement) cardEls.set(key, el)
}

function onContinue() {
  submitAttempted.value = true
  const firstMissing = cards.value.find((card) => card.missing)
  if (firstMissing) {
    cardEls.get(firstMissing.key)?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    return
  }
  // Écran 2 (coordonnées + validation) : maquette à venir.
  console.log('stay tuned')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.recap-evenement {
  display: flex;
  justify-content: center;
  flex: 1;
  background: $surface-white;

  .column {
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 30rem;
    padding: $spacing-m $section-padding-x $spacing-xl;

    .top-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: $spacing-m;
      margin-bottom: $spacing-l;

      .back {
        display: inline-flex;
        align-items: center;
        gap: $spacing-xxs;
        padding: 0;
        border: none;
        background: none;
        color: $text-primary;
        font-family: inherit;
        font-size: $font-size-sm;
        cursor: pointer;

        .icon {
          width: 1.125rem;
          height: 1.125rem;
        }
      }

      .prestataire {
        display: flex;
        align-items: center;
        gap: $spacing-xs;
        min-width: 0;

        .prestataire-name {
          overflow: hidden;
          color: $text-secondary;
          font-size: $font-size-xs;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .prestataire-avatar {
          flex-shrink: 0;
          width: 2.5rem;
          height: 2.5rem;
          border-radius: 50%;
          overflow: hidden;
        }
      }
    }

    .title {
      margin: 0;
      color: $text-primary;
      font-family: 'Cormorant Garamond', serif;
      font-size: clamp(1.75rem, 7vw, 2.25rem);
      font-weight: $font-weight-medium;
      line-height: 1.2;
    }

    .subtitle {
      margin: $spacing-xs 0 0;
      color: $text-secondary;
      font-size: $font-size-md;
      line-height: $line-height-normal;
    }

    // ── Cartes ─────────────────────────────────────────────────────────────────
    .cards {
      display: flex;
      flex-direction: column;
      gap: $spacing-s;
      margin-top: $spacing-l;

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

    // ── Conseil ────────────────────────────────────────────────────────────────
    .tip {
      display: flex;
      align-items: flex-start;
      gap: $spacing-s;
      margin-top: $spacing-l;
      padding: $spacing-m;
      border-radius: $radius-lg;
      background: rgba($brand-accent, 0.1);

      .icon {
        flex-shrink: 0;
        width: 1.75rem;
        height: 1.75rem;
        color: $brand-accent;
      }

      .tip-text {
        color: $text-secondary;
        font-size: $font-size-xs;
        line-height: $line-height-normal;

        .tip-title {
          display: block;
          color: $text-primary;
          font-size: $font-size-sm;
        }
      }
    }
  }
}

// Même bouton que le CTA du tunnel /organisation. Hors de .recap-evenement : le panneau est
// téléporté hors de ce conteneur par le drawer.
.cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-xs;
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

  .icon {
    width: 1.25rem;
    height: 1.25rem;
  }
}

.sheet-body {
  display: flex;
  flex-direction: column;
  padding: 0 $spacing-m $spacing-l;
}
</style>
