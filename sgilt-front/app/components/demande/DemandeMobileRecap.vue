<template>
  <div class="mobile-recap">
    <!-- ── Liste des items ────────────────────────────────────────────────────── -->
    <div class="recap-list">
      <!-- ── Header ── -->
      <div class="recap-header">
        <span class="recap-title">{{ $t('tunnel.recap-mobile.header-title') }}</span>
        <button class="cancel-btn" type="button" @click="showCancelDialog = true">
          {{ $t('tunnel.cancel.label') }}
        </button>
      </div>

      <!-- Card prestataire + date -->
      <SgiltContentCard class="recap-card presta-card">
        <img class="presta-img" :src="state.prestataireImage" :alt="state.prestataireName" />
        <div class="presta-info">
          <span class="presta-name">{{ state.prestataireName }}</span>
          <span v-if="state.date" class="presta-date">{{ formatDate(state.date) }}</span>
        </div>
      </SgiltContentCard>

      <template v-for="item in items" :key="item.key">
        <!-- Card individuelle -->
        <SgiltContentCard
          v-if="item.type === 'individual'"
          :ref="
            (el) => {
              if (el) itemEls[item.key] = el as HTMLElement
            }
          "
          class="recap-card recap-card--individual"
          :class="{
            'is-incomplete': item.required && !item.value && !submitAttempted,
            'has-error': item.required && !item.value && submitAttempted,
            highlighted: highlightedField === item.key,
          }"
        >
          <span class="card-icon">{{ item.emoji }}</span>
          <div class="card-content">
            <div class="card-title-row">
              <span
                class="card-label"
                :class="{ 'card-label--error': item.required && !item.value && submitAttempted }"
              >
                {{ item.label }}
                <span v-if="!item.required && !item.value" class="optional-tag">(optionnel)</span>
              </span>
              <button class="card-btn" type="button" @click="openSheet(item.key)">
                {{ item.value ? $t('tunnel.recap-mobile.edit') : $t('tunnel.recap-mobile.add') }}
              </button>
            </div>
            <span
              class="card-value"
              :class="{
                'card-value--empty': !item.value,
                'card-value--error': item.required && !item.value && submitAttempted,
              }"
            >
              {{
                item.value ??
                (submitAttempted && item.required ? $t('tunnel.recap-mobile.missing') : 'Ajouter')
              }}
            </span>
          </div>
        </SgiltContentCard>

        <!-- Card groupée -->
        <SgiltContentCard
          v-else
          :ref="
            (el) => {
              if (el) itemEls[item.key] = el as HTMLElement
            }
          "
          class="recap-card"
          :class="{
            'is-incomplete': item.subFields.some((s) => s.isMissing) && !submitAttempted,
            'has-error': item.subFields.some((s) => s.isMissing) && submitAttempted,
            highlighted: highlightedField === item.key,
          }"
        >
          <div class="card-row">
            <span class="card-icon">{{ item.emoji }}</span>
            <span class="card-label">{{ item.label }}</span>
            <button class="card-btn" type="button" @click="activeGroupSheet = item.key">
              {{ $t('tunnel.recap-mobile.edit') }}
            </button>
          </div>
          <div class="card-subfields">
            <div v-for="sub in item.subFields" :key="sub.key" class="subfield">
              <span
                class="subfield-label"
                :class="{ 'subfield-label--error': sub.isMissing && submitAttempted }"
              >
                {{ sub.label }}
                <span v-if="!sub.required && !sub.value" class="optional-tag">(optionnel)</span>
                <span v-else-if="sub.isMissing && submitAttempted" class="required-warning">*</span>
              </span>
              <span class="subfield-value" :class="{ 'subfield-value--empty': !sub.value }">
                {{ sub.value ?? '' }}
              </span>
            </div>
          </div>
        </SgiltContentCard>
      </template>
    </div>

    <!-- ── Bouton sticky ──────────────────────────────────────────────────────── -->
    <div class="recap-footer">
      <p v-if="submitError" class="submit-error">{{ submitError }}</p>
      <SgiltButton class="submit-btn" :loading="submitting" @click="handleSubmit">
        {{ $t('tunnel.recap-mobile.submit') }}
      </SgiltButton>
    </div>

    <!-- ── Sheet individuelle (options + textarea) ────────────────────────────── -->
    <SgiltBottomSheet v-model:open="sheetOpen" :title="activeIndividualItem?.label ?? ''">
      <div v-if="activeField === 'eventType'" class="sheet-option-body">
        <DemandeOptionSelect
          :options="EVENT_TYPE_OPTIONS"
          :model-value="state.eventType"
          :autre-value="state.eventTypeAutre"
          autre-placeholder="Quel événement préparez-vous ?"
          @update:model-value="state.eventType = $event"
          @update:autre-value="state.eventTypeAutre = $event"
          @change="sheetOpen = false"
        />
      </div>
      <div v-else-if="activeField === 'ambiance'" class="sheet-option-body">
        <DemandeOptionSelect
          :options="AMBIANCE_OPTIONS"
          :model-value="state.ambiance"
          :autre-value="state.ambianceAutre"
          autre-placeholder="Décrivez l'ambiance souhaitée…"
          @update:model-value="state.ambiance = $event"
          @update:autre-value="state.ambianceAutre = $event"
          @change="sheetOpen = false"
        />
      </div>
      <div v-else-if="activeField === 'momentCle'" class="sheet-option-body">
        <DemandeOptionSelect
          :options="MOMENT_CLE_OPTIONS"
          :model-value="state.momentCle"
          :autre-value="state.momentCleAutre"
          autre-placeholder="Décrivez le moment clé…"
          @update:model-value="state.momentCle = $event"
          @update:autre-value="state.momentCleAutre = $event"
          @change="sheetOpen = false"
        />
      </div>
      <div v-else-if="activeField" class="sheet-field-body">
        <textarea
          ref="textareaRef"
          v-model="individualFieldModel"
          class="field-input field-textarea"
          :placeholder="activeIndividualItem?.placeholder ?? ''"
          rows="5"
        />
        <SgiltButton class="validate-btn" @click="sheetOpen = false">
          {{ $t('tunnel.recap-mobile.validate') }}
        </SgiltButton>
      </div>
    </SgiltBottomSheet>

    <!-- ── Sheet groupée : Détails pratiques ────────────────────────────────── -->
    <SgiltBottomSheet
      v-model:open="detailsSheetOpen"
      :title="detailsPratiquesItem?.label ?? ''"
      overlay
    >
      <SgiltDemandeFieldGroup
        v-if="detailsPratiquesItem && detailsSheetOpen"
        v-model="detailsPratiquesItem"
        @complete="fermerBottomSheet('detailsPratiques')"
      />
    </SgiltBottomSheet>

    <!-- ── Sheet groupée : Vos coordonnées ───────────────────────────────────── -->
    <SgiltBottomSheet
      v-model:open="coordonneesSheetOpen"
      :title="coordonneesItem?.label ?? ''"
      overlay
    >
      <SgiltDemandeFieldGroup
        v-if="coordonneesItem && coordonneesSheetOpen"
        v-model="coordonneesItem"
        @complete="fermerBottomSheet('coordonnees')"
      />
    </SgiltBottomSheet>

    <!-- ── Dialog annulation ──────────────────────────────────────────────────── -->
    <SgiltConfirmDialog
      v-model:open="showCancelDialog"
      :title="$t('tunnel.cancel.dialog-title')"
      :message="$t('tunnel.cancel.dialog-body')"
      :confirm-label="$t('tunnel.cancel.confirm')"
      :cancel-label="$t('tunnel.cancel.back')"
      @confirm="$emit('cancel')"
    />
  </div>
</template>

<script setup lang="ts">
import SgiltBottomSheet from '~/components/basics/sheets/SgiltBottomSheet.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltConfirmDialog from '~/components/basics/dialogs/SgiltConfirmDialog.vue'
import SgiltContentCard from '~/components/basics/cards/SgiltContentCard.vue'
import SgiltDemandeFieldGroup from '~/components/basics/SgiltDemandeFieldGroup.vue'
import DemandeOptionSelect from '~/components/demande/DemandeOptionSelect.vue'
import { useDemande } from '~/composables/useDemande'
import { EVENT_TYPE_OPTIONS, AMBIANCE_OPTIONS, MOMENT_CLE_OPTIONS } from '~/types/demande'

defineEmits<{ cancel: [] }>()

const { t } = useI18n()
const isNewEventFlow = computed(() => useFlow().currentFlow.value === 'new-event')

const {
  state,
  submit,
  submitting,
  submitError,
  eventTypeLabel,
  eventTypeEmoji,
  ambianceLabel,
  ambianceEmoji,
  momentCleLabel,
  momentCleEmoji,
} = useDemande()

// ── Validators ────────────────────────────────────────────────────────────────

function validateEmail(v: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v.trim())
}
function validatePhone(v: string): boolean {
  const digits = v.replace(/[\s\-.()\/+]/g, '')
  return /^\d{7,15}$/.test(digits)
}

// ── Types ─────────────────────────────────────────────────────────────────────

type IndividualFieldKey =
  | 'eventType'
  | 'ambiance'
  | 'momentCle'
  | 'description'
  | 'prestataireMessage'
type GroupKey = 'detailsPratiques' | 'coordonnees'
type EditType = 'eventType' | 'ambiance' | 'momentCle' | 'textarea'

interface SubField {
  key: string
  label: string
  placeholder?: string
  required: boolean
  value: string | null
  isMissing: boolean
  type?: string
  autocomplete?: string
  enterkeyhint?: string
  validate?: (value: string) => string | null
}

interface RecapIndividualItem {
  type: 'individual'
  key: IndividualFieldKey
  label: string
  emoji: string
  required: boolean
  editType: EditType
  placeholder?: string
  value: string | null
}

interface RecapGroupItem {
  type: 'group'
  key: GroupKey
  label: string
  emoji: string
  subFields: SubField[]
}

type RecapItem = RecapIndividualItem | RecapGroupItem

// ── Items ─────────────────────────────────────────────────────────────────────

const items = computed((): RecapItem[] => [
  ...(!isNewEventFlow.value
    ? [
        {
          type: 'group' as const,
          key: 'coordonnees' as const,
          label: t('tunnel.recap-mobile.items.coordonnees'),
          emoji: '👤',
          subFields: [
            {
              key: 'prenom',
              label: t('tunnel.recap-mobile.items.prenom'),
              placeholder: t('tunnel.etape6.prenom-placeholder'),
              required: true,
              value: state.prenom || null,
              isMissing: !state.prenom.trim(),
              type: 'text',
              autocomplete: 'given-name',
              enterkeyhint: 'next',
              validate: (v: string) => (!v.trim() ? t('tunnel.etape6.error-required') : null),
            },
            {
              key: 'nom',
              label: t('tunnel.recap-mobile.items.nom'),
              placeholder: t('tunnel.etape6.nom-placeholder'),
              required: true,
              value: state.nom || null,
              isMissing: !state.nom.trim(),
              type: 'text',
              autocomplete: 'family-name',
              enterkeyhint: 'next',
              validate: (v: string) => (!v.trim() ? t('tunnel.etape6.error-required') : null),
            },
            {
              key: 'email',
              label: t('tunnel.recap-mobile.items.email'),
              placeholder: t('tunnel.etape6.email-placeholder'),
              required: true,
              value: state.email || null,
              isMissing: !state.email.trim() || !validateEmail(state.email),
              type: 'email',
              autocomplete: 'email',
              enterkeyhint: 'next',
              validate: (v: string) =>
                !v.trim()
                  ? t('tunnel.etape6.error-required')
                  : !validateEmail(v)
                    ? t('tunnel.etape6.error-email')
                    : null,
            },
            {
              key: 'telephone',
              label: t('tunnel.recap-mobile.items.telephone'),
              placeholder: t('tunnel.etape6.phone-placeholder'),
              required: true,
              value: state.telephone || null,
              isMissing: !state.telephone.trim() || !validatePhone(state.telephone),
              type: 'tel',
              autocomplete: 'tel',
              enterkeyhint: 'done',
              validate: (v: string) =>
                !v.trim()
                  ? t('tunnel.etape6.error-required')
                  : !validatePhone(v)
                    ? t('tunnel.etape6.error-phone')
                    : null,
            },
          ],
        },
      ]
    : []),
  {
    type: 'group',
    key: 'detailsPratiques',
    label: t('tunnel.recap-mobile.items.details-pratiques'),
    emoji: '📍',
    subFields: [
      {
        key: 'ville',
        label: t('tunnel.recap-mobile.items.ville'),
        placeholder: t('tunnel.etape5.city-placeholder'),
        required: true,
        value: state.ville || null,
        isMissing: !state.ville.trim(),
        type: 'text',
        enterkeyhint: 'next',
      },
      {
        key: 'lieu',
        label: t('tunnel.recap-mobile.items.lieu'),
        required: false,
        value: state.lieuDefini && state.lieu ? state.lieu : null,
        isMissing: false,
        type: 'text',
        enterkeyhint: 'next',
      },
      {
        key: 'nbInvites',
        label: t('tunnel.recap-mobile.items.nb-invites'),
        required: false,
        placeholder: t('tunnel.etape5.guests-placeholder'),
        value: state.nbInvites || null,
        isMissing: false,
        type: 'text',
        enterkeyhint: 'done',
      },
    ],
  },
  {
    type: 'individual',
    key: 'prestataireMessage',
    label: t('tunnel.recap-mobile.items.message'),
    emoji: '💬',
    required: false,
    editType: 'textarea',
    placeholder: t('tunnel.etape6.message-placeholder'),
    value: state.prestataireMessage || null,
  },
  {
    type: 'individual',
    key: 'eventType',
    label: t('tunnel.recap-mobile.items.event-type'),
    emoji: eventTypeEmoji.value || '🎉',
    required: true,
    editType: 'eventType',
    value: eventTypeLabel.value,
  },
  {
    type: 'individual',
    key: 'ambiance',
    label: t('tunnel.recap-mobile.items.ambiance'),
    emoji: ambianceEmoji.value || '✨',
    required: true,
    editType: 'ambiance',
    value: ambianceLabel.value,
  },
  {
    type: 'individual',
    key: 'momentCle',
    label: t('tunnel.recap-mobile.items.moment-cle'),
    emoji: momentCleEmoji.value || '⭐',
    required: true,
    editType: 'momentCle',
    value: momentCleLabel.value,
  },
  {
    type: 'individual',
    key: 'description',
    label: t('tunnel.recap-mobile.items.description'),
    emoji: '📝',
    required: false,
    editType: 'textarea',
    placeholder: t('tunnel.etape4.placeholder'),
    value: state.description || null,
  },
])

// ── Sheet individuelle ────────────────────────────────────────────────────────

const activeField = ref<IndividualFieldKey | null>(null)
const sheetOpen = computed({
  get: () => !!activeField.value,
  set: (v) => {
    if (!v) activeField.value = null
  },
})

const activeIndividualItem = computed(() =>
  activeField.value
    ? ((items.value.find((i) => i.key === activeField.value) as RecapIndividualItem | undefined) ??
      null)
    : null,
)

const textareaRef = ref<HTMLTextAreaElement | null>(null)

function openSheet(key: IndividualFieldKey) {
  activeField.value = key
}

watch(activeField, (field) => {
  if (field === 'description' || field === 'prestataireMessage') {
    nextTick(() => textareaRef.value?.focus())
  }
})

const individualFieldModel = computed<string>({
  get: () => {
    switch (activeField.value) {
      case 'description':
        return state.description
      case 'prestataireMessage':
        return state.prestataireMessage ?? ''
      default:
        return ''
    }
  },
  set: (v) => {
    switch (activeField.value) {
      case 'description':
        state.description = v
        break
      case 'prestataireMessage':
        state.prestataireMessage = v
        break
    }
  },
})

// ── Sheets groupées ───────────────────────────────────────────────────────────

const activeGroupSheet = ref<GroupKey | null>(null)

const detailsSheetOpen = computed({
  get: () => activeGroupSheet.value === 'detailsPratiques',
  set: (v) => {
    activeGroupSheet.value = v ? 'detailsPratiques' : null
  },
})

const coordonneesSheetOpen = computed({
  get: () => activeGroupSheet.value === 'coordonnees',
  set: (v) => {
    activeGroupSheet.value = v ? 'coordonnees' : null
  },
})

const coordonneesItem = computed<RecapGroupItem | undefined>({
  get: () =>
    items.value.find((i): i is RecapGroupItem => i.key === 'coordonnees' && i.type === 'group'),
  set: (updated) => {
    if (!updated) return
    for (const sf of updated.subFields) {
      switch (sf.key) {
        case 'prenom':
          state.prenom = sf.value ?? ''
          break
        case 'nom':
          state.nom = sf.value ?? ''
          break
        case 'email':
          state.email = sf.value ?? ''
          break
        case 'telephone':
          state.telephone = sf.value ?? ''
          break
      }
    }
  },
})

const detailsPratiquesItem = computed<RecapGroupItem | undefined>({
  get: () =>
    items.value.find(
      (i): i is RecapGroupItem => i.key === 'detailsPratiques' && i.type === 'group',
    ),
  set: (updated) => {
    if (!updated) return
    for (const sf of updated.subFields) {
      switch (sf.key) {
        case 'ville':
          state.ville = sf.value ?? ''
          break
        case 'lieu':
          state.lieu = sf.value ?? ''
          state.lieuDefini = !!sf.value
          break
        case 'nbInvites':
          state.nbInvites = sf.value ?? ''
          break
      }
    }
  },
})

function fermerBottomSheet(_key: GroupKey) {
  activeGroupSheet.value = null
}

// ── Submit ────────────────────────────────────────────────────────────────────

type AnyFieldKey =
  | IndividualFieldKey
  | 'ville'
  | 'lieu'
  | 'nbInvites'
  | 'prenom'
  | 'nom'
  | 'email'
  | 'telephone'

const FIELD_TO_ITEM: Record<string, string> = {
  ville: 'detailsPratiques',
  lieu: 'detailsPratiques',
  nbInvites: 'detailsPratiques',
  prenom: 'coordonnees',
  nom: 'coordonnees',
  email: 'coordonnees',
  telephone: 'coordonnees',
}

const REQUIRED_FIELDS = computed((): AnyFieldKey[] => [
  'eventType',
  'ambiance',
  'momentCle',
  'ville',
  ...(!isNewEventFlow.value ? (['prenom', 'nom', 'email', 'telephone'] as AnyFieldKey[]) : []),
])

function isFieldValid(key: AnyFieldKey): boolean {
  switch (key) {
    case 'eventType':
      return !!state.eventType
    case 'ambiance':
      return !!state.ambiance
    case 'momentCle':
      return !!state.momentCle
    case 'ville':
      return !!state.ville.trim()
    case 'prenom':
      return !!state.prenom.trim()
    case 'nom':
      return !!state.nom.trim()
    case 'email':
      return !!state.email.trim() && validateEmail(state.email)
    case 'telephone':
      return !!state.telephone.trim() && validatePhone(state.telephone)
    default:
      return true
  }
}

const itemEls = ref<Record<string, HTMLElement>>({})
const highlightedField = ref<string | null>(null)
const submitAttempted = ref(false)

function handleSubmit() {
  submitAttempted.value = true
  const missing = REQUIRED_FIELDS.value.find((k) => !isFieldValid(k))
  if (missing) {
    const scrollTarget = FIELD_TO_ITEM[missing] ?? missing
    highlightedField.value = scrollTarget
    itemEls.value[scrollTarget]?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    setTimeout(() => {
      if (highlightedField.value === scrollTarget) highlightedField.value = null
    }, 2500)
    return
  }
  submit()
}

// ── Dialog annulation ─────────────────────────────────────────────────────────

const showCancelDialog = ref(false)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ─── Conteneur ────────────────────────────────────────────────────────────────
.mobile-recap {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding-bottom: 70px; // pour éviter que le contenu soit caché derrière le footer fixe
}

// ─── Header ───────────────────────────────────────────────────────────────────
.recap-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $spacing-m $spacing-xs;
  flex-shrink: 0;
  border-bottom: 1px solid $divider-color;
}

.recap-title {
  font-size: 0.95rem;
  font-weight: 500;
  font-style: italic;
  color: $text-primary;
}

.cancel-btn {
  background: none;
  border: none;
  padding: 0;
  font-size: 0.85rem;
  font-family: inherit;
  color: $text-secondary;
  cursor: pointer;
  transition: color 150ms ease;

  &:hover {
    color: $text-primary;
  }
}

// ─── Liste — fond dégradé sgilt ────────────────────────────────────────────────────────
.recap-list {
  flex: 1;
  overflow-y: auto;
  overscroll-behavior: contain;
  background:
    radial-gradient(
      1100px 480px at 50% -5%,
      rgba($brand-accent, 0.16) 0%,
      rgba(255, 255, 255, 0) 55%
    ),
    linear-gradient(180deg, #fffdf6 0%, #ffffff 50%);
  padding: $spacing-m $spacing-m 5rem;
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

// ─── Card de base ─────────────────────────────────────────────────────────────
@keyframes card-pulse {
  0%,
  100% {
    background-color: #fff;
  }
  25%,
  75% {
    background-color: var(--pulse-bg);
  }
}

.recap-card {
  border-left: 3px solid transparent;
  padding: $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  --pulse-bg: #{rgba($brand-accent, 0.1)};

  &.is-incomplete {
    border-left-color: $brand-accent;
  }

  &.has-error {
    border-left-color: $state-error;
    --pulse-bg: #{rgba($state-error, 0.06)};
  }

  &.highlighted {
    animation: card-pulse 2.5s ease;
  }
}

// ─── Card prestataire ─────────────────────────────────────────────────────────
.presta-card {
  flex-direction: row;
  align-items: center;
  gap: $spacing-m;
  border-left-color: transparent;
}

.presta-img {
  width: 3rem;
  height: 3rem;
  border-radius: $radius-md;
  object-fit: cover;
  flex-shrink: 0;
}

.presta-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.presta-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: $text-primary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.presta-date {
  font-size: 0.82rem;
  color: $text-secondary;
}

// ─── Layout individuel : emoji à gauche, contenu à droite ────────────────────
.recap-card--individual {
  flex-direction: row;
  align-items: center;
  gap: $spacing-m;
}

.card-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
  width: 2rem;
  text-align: center;
  align-self: center;
}

.card-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: $spacing-xxs;
}

.card-title-row {
  display: flex;
  align-items: center;
  gap: $spacing-s;
}

// ─── Label ────────────────────────────────────────────────────────────────────
.card-label {
  flex: 1;
  font-size: 0.75rem;
  font-weight: 700;
  color: $text-secondary;
  text-transform: uppercase;
  letter-spacing: 0.05em;

  &--error {
    color: $state-error;
  }
}

.optional-tag {
  font-size: 0.7rem;
  font-weight: 500;
  text-transform: none;
  letter-spacing: 0;
  color: $text-secondary;
  opacity: 0.7;
  margin-left: 3px;
}

// ─── Bouton ───────────────────────────────────────────────────────────────────
.card-btn {
  flex-shrink: 0;
  background: none;
  border: 1px solid $divider-color;
  border-radius: $radius-sm;
  padding: $spacing-xxs $spacing-s;
  font-size: 0.8rem;
  font-family: inherit;
  color: $text-secondary;
  cursor: pointer;
  transition:
    border-color 150ms ease,
    color 150ms ease;

  &:hover {
    border-color: $brand-accent;
    color: $brand-accent;
  }
}

// ─── Valeur ───────────────────────────────────────────────────────────────────
.card-value {
  font-size: 0.92rem;
  color: $text-primary;

  &--empty {
    color: $text-secondary;
    font-style: italic;
  }

  &--error {
    color: $state-error;
    font-weight: 500;
  }
}

// ─── Icône (items groupés — reste dans card-row) ──────────────────────────────
.card-row {
  display: flex;
  align-items: center;
  gap: $spacing-s;

  .card-icon {
    font-size: 1rem;
    width: 1.4rem;
  }
}

// ─── Sous-champs (items groupés) ──────────────────────────────────────────────
.card-subfields {
  display: flex;
  flex-direction: column;
  gap: $spacing-xxs;
  padding-left: calc(1.4rem + $spacing-s);
  border-top: 1px solid $divider-color;
  padding-top: $spacing-xs;
}

.subfield {
  display: flex;
  align-items: baseline;
  gap: $spacing-s;
  font-size: 0.88rem;
}

.subfield-label {
  color: $text-secondary;
  flex-shrink: 0;

  &--error {
    color: $state-error;
    font-weight: 600;
  }
}

.incomplete-hint {
  color: $text-secondary;
  font-style: italic;
  font-size: 0.8rem;
}

.required-warning {
  margin-left: 3px;
  font-size: 0.75rem;
}

.subfield-value {
  color: $text-primary;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;

  &--empty {
    color: $text-secondary;
  }

  &--optional {
    color: $text-secondary;
    font-style: italic;
  }
}

// ─── Footer sticky ────────────────────────────────────────────────────────────
.recap-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 20;
  padding: $spacing-s $spacing-m $spacing-m;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-top: 1px solid $divider-color;
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
}

.submit-btn {
  width: 100%;
  justify-content: center;
}

.submit-error {
  font-size: 0.85rem;
  color: $state-error;
  margin: 0;
}

// ─── Sheet individuelle (options) ─────────────────────────────────────────────
.sheet-option-body {
  padding: $spacing-m;
}

.sheet-field-body {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m;
}

.validate-btn {
  align-self: flex-end;
}

// ─── Contenu des sheets groupées ─────────────────────────────────────────────
.group-sheet-body {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-m;
}

.sheet-field-group {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
}

.sheet-label {
  font-size: 0.85rem;
  font-weight: 600;
  color: $text-primary;
}

.required-star {
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

.field-textarea {
  resize: vertical;
  padding: $spacing-m;
  line-height: 1.6;
}

.field-error {
  font-size: 0.8rem;
  color: $state-error;
  margin: 0;
}

.validate-btn--full {
  width: 100%;
  justify-content: center;
}

// ─── Dialog ───────────────────────────────────────────────────────────────────
.dialog-content {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-m $spacing-m $spacing-l;
}

.dialog-body {
  font-size: 0.95rem;
  color: $text-secondary;
  margin: 0;
  line-height: 1.5;
}

.dialog-actions {
  display: flex;
  gap: $spacing-s;
  justify-content: flex-end;
}
</style>
