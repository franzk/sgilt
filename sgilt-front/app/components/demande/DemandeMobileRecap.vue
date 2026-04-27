<template>
  <div class="mobile-recap">
    <!-- ── Header ──────────────────────────────────────────────────────────────── -->
    <div class="recap-header">
      <span class="recap-title">{{ $t('tunnel.recap-mobile.header-title') }}</span>
      <button class="cancel-btn" type="button" @click="showCancelDialog = true">
        {{ $t('tunnel.cancel.label') }}
      </button>
    </div>

    <!-- ── Liste des items ────────────────────────────────────────────────────── -->
    <div class="recap-list">
      <div
        v-for="item in items"
        :key="item.key"
        :ref="(el) => { if (el) itemEls[item.key] = el as HTMLElement }"
        class="recap-item"
        :class="{ highlighted: highlightedField === item.key }"
      >
        <div class="item-left">
          <span class="item-emoji">{{ item.emoji }}</span>
          <div class="item-info">
            <span class="item-label">{{ item.label }}</span>
            <span
              class="item-value"
              :class="{
                'is-required-missing': item.required && !item.value,
                'is-add': !item.required && !item.value,
              }"
            >
              {{
                item.value
                  ?? (item.required
                    ? $t('tunnel.recap-mobile.missing')
                    : $t('tunnel.recap-mobile.add'))
              }}
            </span>
          </div>
        </div>
        <button class="item-action-btn" type="button" @click="openSheet(item.key)">
          {{ item.value ? $t('tunnel.recap-mobile.edit') : $t('tunnel.recap-mobile.add') }}
        </button>
      </div>
    </div>

    <!-- ── Bouton sticky ──────────────────────────────────────────────────────── -->
    <div class="recap-footer">
      <p v-if="submitError" class="submit-error">{{ submitError }}</p>
      <SgiltButton class="submit-btn" :loading="submitting" @click="handleSubmit">
        {{ $t('tunnel.recap-mobile.submit') }}
      </SgiltButton>
    </div>

    <!-- ── Bottom sheet d'édition ─────────────────────────────────────────────── -->
    <SgiltBottomSheet v-model:open="sheetOpen" :title="activeItem?.label ?? ''">
      <!-- Choix événement -->
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

      <!-- Choix ambiance -->
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

      <!-- Choix moment clé -->
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

      <!-- Champs texte / textarea / email / tel -->
      <div v-else-if="activeField" class="sheet-field-body">
        <textarea
          v-if="activeItem?.editType === 'textarea'"
          v-model="fieldModel"
          class="field-input field-textarea"
          :placeholder="activeItem?.placeholder ?? ''"
          rows="5"
          @focus="sheetFieldError = null"
        />
        <input
          v-else
          v-model="fieldModel"
          class="field-input"
          :type="activeItem?.editType ?? 'text'"
          :autocomplete="activeItem?.autocomplete"
          :placeholder="activeItem?.placeholder ?? ''"
          @blur="validateSheetField"
          @focus="sheetFieldError = null"
        />
        <p v-if="sheetFieldError" class="field-error">{{ sheetFieldError }}</p>
        <SgiltButton
          class="validate-btn"
          :disabled="sheetValidateDisabled"
          @click="confirmSheet"
        >
          {{ $t('tunnel.recap-mobile.validate') }}
        </SgiltButton>
      </div>
    </SgiltBottomSheet>

    <!-- ── Dialog annulation ──────────────────────────────────────────────────── -->
    <SgiltDialog v-model:open="showCancelDialog" :title="$t('tunnel.cancel.dialog-title')">
      <div class="dialog-content">
        <p class="dialog-body">{{ $t('tunnel.cancel.dialog-body') }}</p>
        <div class="dialog-actions">
          <SgiltButton variant="secondary" @click="showCancelDialog = false">
            {{ $t('tunnel.cancel.back') }}
          </SgiltButton>
          <SgiltButton @click="$emit('cancel')">
            {{ $t('tunnel.cancel.confirm') }}
          </SgiltButton>
        </div>
      </div>
    </SgiltDialog>
  </div>
</template>

<script setup lang="ts">
import SgiltBottomSheet from '~/components/basics/sheets/SgiltBottomSheet.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import DemandeOptionSelect from '~/components/demande/DemandeOptionSelect.vue'
import { useDemande } from '~/composables/useDemande'
import {
  EVENT_TYPE_OPTIONS,
  AMBIANCE_OPTIONS,
  MOMENT_CLE_OPTIONS,
} from '~/types/demande'

defineEmits<{ cancel: [] }>()

const { t } = useI18n()

const {
  state,
  submit,
  submitting,
  submitError,
  eventTypeLabel,
  ambianceLabel,
  momentCleLabel,
} = useDemande()

// ── Types ─────────────────────────────────────────────────────────────────────

type FieldKey =
  | 'eventType' | 'ambiance' | 'momentCle'
  | 'description' | 'ville' | 'nbInvites' | 'lieu'
  | 'prenom' | 'nom' | 'email' | 'telephone' | 'prestataireMessage'

type EditType = 'eventType' | 'ambiance' | 'momentCle' | 'text' | 'textarea' | 'email' | 'tel'

interface RecapItem {
  key: FieldKey
  label: string
  emoji: string
  required: boolean
  editType: EditType
  autocomplete?: string
  placeholder?: string
  value: string | null
}

// ── Items ─────────────────────────────────────────────────────────────────────

const items = computed((): RecapItem[] => [
  {
    key: 'eventType',
    label: t('tunnel.recap-mobile.items.event-type'),
    emoji: '🎉',
    required: true,
    editType: 'eventType',
    value: eventTypeLabel.value,
  },
  {
    key: 'ambiance',
    label: t('tunnel.recap-mobile.items.ambiance'),
    emoji: '✨',
    required: true,
    editType: 'ambiance',
    value: ambianceLabel.value,
  },
  {
    key: 'momentCle',
    label: t('tunnel.recap-mobile.items.moment-cle'),
    emoji: '⭐',
    required: true,
    editType: 'momentCle',
    value: momentCleLabel.value,
  },
  {
    key: 'description',
    label: t('tunnel.recap-mobile.items.description'),
    emoji: '📝',
    required: false,
    editType: 'textarea',
    placeholder: t('tunnel.etape4.placeholder'),
    value: state.description || null,
  },
  {
    key: 'ville',
    label: t('tunnel.recap-mobile.items.ville'),
    emoji: '📍',
    required: true,
    editType: 'text',
    placeholder: t('tunnel.etape5.city-placeholder'),
    value: state.ville || null,
  },
  {
    key: 'nbInvites',
    label: t('tunnel.recap-mobile.items.nb-invites'),
    emoji: '👥',
    required: false,
    editType: 'text',
    placeholder: t('tunnel.etape5.guests-placeholder'),
    value: state.nbInvites || null,
  },
  {
    key: 'lieu',
    label: t('tunnel.recap-mobile.items.lieu'),
    emoji: '🏛️',
    required: false,
    editType: 'text',
    value: state.lieuDefini && state.lieu ? state.lieu : null,
  },
  {
    key: 'prenom',
    label: t('tunnel.recap-mobile.items.prenom'),
    emoji: '👤',
    required: true,
    editType: 'text',
    autocomplete: 'given-name',
    placeholder: t('tunnel.etape6.prenom-placeholder'),
    value: state.prenom || null,
  },
  {
    key: 'nom',
    label: t('tunnel.recap-mobile.items.nom'),
    emoji: '👤',
    required: true,
    editType: 'text',
    autocomplete: 'family-name',
    placeholder: t('tunnel.etape6.nom-placeholder'),
    value: state.nom || null,
  },
  {
    key: 'email',
    label: t('tunnel.recap-mobile.items.email'),
    emoji: '📧',
    required: true,
    editType: 'email',
    autocomplete: 'email',
    placeholder: 'votre@email.fr',
    value: state.email || null,
  },
  {
    key: 'telephone',
    label: t('tunnel.recap-mobile.items.telephone'),
    emoji: '📱',
    required: true,
    editType: 'tel',
    autocomplete: 'tel',
    placeholder: t('tunnel.etape6.phone-placeholder'),
    value: state.telephone || null,
  },
  {
    key: 'prestataireMessage',
    label: t('tunnel.recap-mobile.items.message'),
    emoji: '💬',
    required: false,
    editType: 'textarea',
    placeholder: t('tunnel.etape6.message-placeholder'),
    value: state.prestataireMessage || null,
  },
])

// ── Sheet ─────────────────────────────────────────────────────────────────────

const activeField = ref<FieldKey | null>(null)
const sheetOpen = computed({
  get: () => !!activeField.value,
  set: (v) => { if (!v) activeField.value = null },
})

const activeItem = computed(() =>
  activeField.value ? items.value.find((i) => i.key === activeField.value) ?? null : null,
)

function openSheet(key: FieldKey) {
  sheetFieldError.value = null
  activeField.value = key
}

// Lecture/écriture dans state selon le champ actif
const fieldModel = computed<string>({
  get: () => {
    switch (activeField.value) {
      case 'description': return state.description
      case 'ville': return state.ville
      case 'nbInvites': return state.nbInvites
      case 'lieu': return state.lieu
      case 'prenom': return state.prenom
      case 'nom': return state.nom
      case 'email': return state.email
      case 'telephone': return state.telephone
      case 'prestataireMessage': return state.prestataireMessage ?? ''
      default: return ''
    }
  },
  set: (v) => {
    switch (activeField.value) {
      case 'description': state.description = v; break
      case 'ville': state.ville = v; break
      case 'nbInvites': state.nbInvites = v; break
      case 'lieu':
        state.lieu = v
        state.lieuDefini = !!v
        break
      case 'prenom': state.prenom = v; break
      case 'nom': state.nom = v; break
      case 'email': state.email = v; break
      case 'telephone': state.telephone = v; break
      case 'prestataireMessage': state.prestataireMessage = v; break
    }
  },
})

// ── Validation sheet ──────────────────────────────────────────────────────────

const sheetFieldError = ref<string | null>(null)

function validateEmail(v: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v.trim())
}
function validatePhone(v: string): boolean {
  const digits = v.replace(/[\s\-.()\/+]/g, '')
  return /^\d{7,15}$/.test(digits)
}

function validateSheetField() {
  sheetFieldError.value = null
  const v = fieldModel.value
  if (!v) return
  if (activeField.value === 'email' && !validateEmail(v)) {
    sheetFieldError.value = t('tunnel.etape6.error-email')
  } else if (activeField.value === 'telephone' && !validatePhone(v)) {
    sheetFieldError.value = t('tunnel.etape6.error-phone')
  }
}

const sheetValidateDisabled = computed(() => {
  const v = fieldModel.value
  if (!v) return false
  if (activeField.value === 'email') return !validateEmail(v)
  if (activeField.value === 'telephone') return !validatePhone(v)
  return false
})

function confirmSheet() {
  if (sheetValidateDisabled.value) return
  sheetOpen.value = false
}

// ── Submit ────────────────────────────────────────────────────────────────────

const REQUIRED: FieldKey[] = [
  'eventType', 'ambiance', 'momentCle', 'ville', 'prenom', 'nom', 'email', 'telephone',
]

function isFieldValid(key: FieldKey): boolean {
  switch (key) {
    case 'eventType': return !!state.eventType
    case 'ambiance': return !!state.ambiance
    case 'momentCle': return !!state.momentCle
    case 'ville': return !!state.ville.trim()
    case 'prenom': return !!state.prenom.trim()
    case 'nom': return !!state.nom.trim()
    case 'email': return !!state.email.trim() && validateEmail(state.email)
    case 'telephone': return !!state.telephone.trim() && validatePhone(state.telephone)
    default: return true
  }
}

const itemEls = ref<Record<string, HTMLElement>>({})
const highlightedField = ref<string | null>(null)

function handleSubmit() {
  const missing = REQUIRED.find((k) => !isFieldValid(k))
  if (missing) {
    highlightedField.value = missing
    itemEls.value[missing]?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    setTimeout(() => {
      if (highlightedField.value === missing) highlightedField.value = null
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
}

// ─── Header ───────────────────────────────────────────────────────────────────
.recap-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-s $spacing-m $spacing-xs;
  flex-shrink: 0;
  border-bottom: 1px solid $divider-color;
}

.recap-title {
  font-size: 0.95rem;
  font-weight: 600;
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

// ─── Liste ────────────────────────────────────────────────────────────────────
.recap-list {
  flex: 1;
  overflow-y: auto;
  overscroll-behavior: contain;
}

.recap-item {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: $spacing-m $spacing-m;
  border-bottom: 1px solid $divider-color;
  transition: background-color 300ms ease;

  &.highlighted {
    animation: error-pulse 2.5s ease;
  }
}

@keyframes error-pulse {
  0%, 100% { background-color: transparent; }
  25%, 75% { background-color: rgba($state-error, 0.07); }
}

.item-left {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  flex: 1;
  min-width: 0;
}

.item-emoji {
  font-size: 1.1rem;
  flex-shrink: 0;
  width: 1.5rem;
  text-align: center;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.item-label {
  font-size: 0.78rem;
  font-weight: 600;
  color: $text-secondary;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.item-value {
  font-size: 0.92rem;
  color: $text-primary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;

  &.is-add {
    color: $text-secondary;
    font-style: italic;
    font-size: 0.88rem;
  }

  &.is-required-missing {
    color: $state-error;
    font-weight: 500;
    font-size: 0.88rem;

    &::before {
      content: '⚠ ';
      font-style: normal;
    }
  }
}

.item-action-btn {
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

// ─── Footer sticky ────────────────────────────────────────────────────────────
.recap-footer {
  flex-shrink: 0;
  padding: $spacing-s $spacing-m $spacing-m;
  background: #fff;
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

// ─── Sheet options ────────────────────────────────────────────────────────────
.sheet-option-body {
  padding: $spacing-m;
}

// ─── Sheet champs ─────────────────────────────────────────────────────────────
.sheet-field-body {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m;
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

.validate-btn {
  align-self: flex-end;
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
