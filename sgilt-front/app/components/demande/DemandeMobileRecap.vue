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
      <template v-for="item in items" :key="item.key">
        <!-- Item individuel -->
        <div
          v-if="item.type === 'individual'"
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

        <!-- Item groupé -->
        <div
          v-else
          :ref="(el) => { if (el) itemEls[item.key] = el as HTMLElement }"
          class="recap-item recap-item--group"
          :class="{ highlighted: highlightedField === item.key }"
        >
          <div class="group-header">
            <div class="item-left">
              <span class="item-emoji">{{ item.emoji }}</span>
              <span class="group-label">{{ item.label }}</span>
            </div>
            <button class="item-action-btn" type="button" @click="activeGroupSheet = item.key">
              {{ $t('tunnel.recap-mobile.edit') }}
            </button>
          </div>
          <div class="group-subfields">
            <div v-for="sub in item.subFields" :key="sub.key" class="subfield">
              <span class="subfield-label" :class="{ 'is-required-missing': sub.isMissing }">
                {{ sub.label }}
                <span v-if="sub.isMissing" class="required-warning">⚠</span>
              </span>
              <span class="subfield-value" :class="{ 'is-empty': !sub.value }">
                {{ sub.value ?? '—' }}
              </span>
            </div>
          </div>
        </div>
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
      :title="$t('tunnel.recap-mobile.items.details-pratiques')"
      overlay
      fullscreen
    >
      <div class="group-sheet-body">
        <div class="sheet-field-group">
          <label class="sheet-label">
            {{ $t('tunnel.recap-mobile.items.ville') }}
            <span class="required-star">*</span>
          </label>
          <input
            v-model="state.ville"
            class="field-input"
            type="text"
            :placeholder="$t('tunnel.etape5.city-placeholder')"
          />
        </div>

        <div class="sheet-field-group">
          <label class="sheet-label">{{ $t('tunnel.recap-mobile.items.lieu') }}</label>
          <input v-model="lieu" class="field-input" type="text" />
        </div>

        <div class="sheet-field-group">
          <label class="sheet-label">{{ $t('tunnel.recap-mobile.items.nb-invites') }}</label>
          <input
            v-model="state.nbInvites"
            class="field-input"
            type="text"
            :placeholder="$t('tunnel.etape5.guests-placeholder')"
          />
        </div>

        <SgiltButton class="validate-btn--full" @click="detailsSheetOpen = false">
          {{ $t('tunnel.recap-mobile.validate') }}
        </SgiltButton>
      </div>
    </SgiltBottomSheet>

    <!-- ── Sheet groupée : Vos coordonnées ───────────────────────────────────── -->
    <SgiltBottomSheet
      v-model:open="coordonneesSheetOpen"
      :title="$t('tunnel.recap-mobile.items.coordonnees')"
      overlay
      fullscreen
    >
      <div class="group-sheet-body">
        <div class="sheet-field-group">
          <label class="sheet-label">
            {{ $t('tunnel.recap-mobile.items.prenom') }}
            <span class="required-star">*</span>
          </label>
          <input
            v-model="state.prenom"
            class="field-input"
            type="text"
            autocomplete="given-name"
            :placeholder="$t('tunnel.etape6.prenom-placeholder')"
          />
        </div>

        <div class="sheet-field-group">
          <label class="sheet-label">
            {{ $t('tunnel.recap-mobile.items.nom') }}
            <span class="required-star">*</span>
          </label>
          <input
            v-model="state.nom"
            class="field-input"
            type="text"
            autocomplete="family-name"
            :placeholder="$t('tunnel.etape6.nom-placeholder')"
          />
        </div>

        <div class="sheet-field-group">
          <label class="sheet-label">
            {{ $t('tunnel.recap-mobile.items.email') }}
            <span class="required-star">*</span>
          </label>
          <input
            v-model="state.email"
            class="field-input"
            type="email"
            autocomplete="email"
            placeholder="votre@email.fr"
            @blur="validateCoordonneesField('email')"
            @focus="coordonneesErrors.email = null"
          />
          <p v-if="coordonneesErrors.email" class="field-error">{{ coordonneesErrors.email }}</p>
        </div>

        <div class="sheet-field-group">
          <label class="sheet-label">
            {{ $t('tunnel.recap-mobile.items.telephone') }}
            <span class="required-star">*</span>
          </label>
          <input
            v-model="state.telephone"
            class="field-input"
            type="tel"
            autocomplete="tel"
            :placeholder="$t('tunnel.etape6.phone-placeholder')"
            @blur="validateCoordonneesField('telephone')"
            @focus="coordonneesErrors.telephone = null"
          />
          <p v-if="coordonneesErrors.telephone" class="field-error">{{ coordonneesErrors.telephone }}</p>
        </div>

        <SgiltButton
          class="validate-btn--full"
          :disabled="coordonneesValidateDisabled"
          @click="coordonneesSheetOpen = false"
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
import { EVENT_TYPE_OPTIONS, AMBIANCE_OPTIONS, MOMENT_CLE_OPTIONS } from '~/types/demande'

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

// ── Validators ────────────────────────────────────────────────────────────────

function validateEmail(v: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v.trim())
}
function validatePhone(v: string): boolean {
  const digits = v.replace(/[\s\-.()\/+]/g, '')
  return /^\d{7,15}$/.test(digits)
}

// ── Types ─────────────────────────────────────────────────────────────────────

type IndividualFieldKey = 'eventType' | 'ambiance' | 'momentCle' | 'description' | 'prestataireMessage'
type GroupKey = 'detailsPratiques' | 'coordonnees'
type EditType = 'eventType' | 'ambiance' | 'momentCle' | 'textarea'

interface SubField {
  key: string
  label: string
  required: boolean
  value: string | null
  isMissing: boolean
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
  {
    type: 'individual',
    key: 'eventType',
    label: t('tunnel.recap-mobile.items.event-type'),
    emoji: '🎉',
    required: true,
    editType: 'eventType',
    value: eventTypeLabel.value,
  },
  {
    type: 'individual',
    key: 'ambiance',
    label: t('tunnel.recap-mobile.items.ambiance'),
    emoji: '✨',
    required: true,
    editType: 'ambiance',
    value: ambianceLabel.value,
  },
  {
    type: 'individual',
    key: 'momentCle',
    label: t('tunnel.recap-mobile.items.moment-cle'),
    emoji: '⭐',
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
  {
    type: 'group',
    key: 'detailsPratiques',
    label: t('tunnel.recap-mobile.items.details-pratiques'),
    emoji: '📍',
    subFields: [
      {
        key: 'ville',
        label: t('tunnel.recap-mobile.items.ville'),
        required: true,
        value: state.ville || null,
        isMissing: !state.ville.trim(),
      },
      {
        key: 'lieu',
        label: t('tunnel.recap-mobile.items.lieu'),
        required: false,
        value: state.lieuDefini && state.lieu ? state.lieu : null,
        isMissing: false,
      },
      {
        key: 'nbInvites',
        label: t('tunnel.recap-mobile.items.nb-invites'),
        required: false,
        value: state.nbInvites || null,
        isMissing: false,
      },
    ],
  },
  {
    type: 'group',
    key: 'coordonnees',
    label: t('tunnel.recap-mobile.items.coordonnees'),
    emoji: '👤',
    subFields: [
      {
        key: 'prenom',
        label: t('tunnel.recap-mobile.items.prenom'),
        required: true,
        value: state.prenom || null,
        isMissing: !state.prenom.trim(),
      },
      {
        key: 'nom',
        label: t('tunnel.recap-mobile.items.nom'),
        required: true,
        value: state.nom || null,
        isMissing: !state.nom.trim(),
      },
      {
        key: 'email',
        label: t('tunnel.recap-mobile.items.email'),
        required: true,
        value: state.email || null,
        isMissing: !state.email.trim() || !validateEmail(state.email),
      },
      {
        key: 'telephone',
        label: t('tunnel.recap-mobile.items.telephone'),
        required: true,
        value: state.telephone || null,
        isMissing: !state.telephone.trim() || !validatePhone(state.telephone),
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
])

// ── Sheet individuelle ────────────────────────────────────────────────────────

const activeField = ref<IndividualFieldKey | null>(null)
const sheetOpen = computed({
  get: () => !!activeField.value,
  set: (v) => { if (!v) activeField.value = null },
})

const activeIndividualItem = computed(() =>
  activeField.value
    ? (items.value.find((i) => i.key === activeField.value) as RecapIndividualItem | undefined) ?? null
    : null,
)

function openSheet(key: IndividualFieldKey) {
  activeField.value = key
}

const individualFieldModel = computed<string>({
  get: () => {
    switch (activeField.value) {
      case 'description': return state.description
      case 'prestataireMessage': return state.prestataireMessage ?? ''
      default: return ''
    }
  },
  set: (v) => {
    switch (activeField.value) {
      case 'description': state.description = v; break
      case 'prestataireMessage': state.prestataireMessage = v; break
    }
  },
})

// ── Sheets groupées ───────────────────────────────────────────────────────────

const activeGroupSheet = ref<GroupKey | null>(null)

const detailsSheetOpen = computed({
  get: () => activeGroupSheet.value === 'detailsPratiques',
  set: (v) => { activeGroupSheet.value = v ? 'detailsPratiques' : null },
})

const coordonneesSheetOpen = computed({
  get: () => activeGroupSheet.value === 'coordonnees',
  set: (v) => { activeGroupSheet.value = v ? 'coordonnees' : null },
})

// Binding intermédiaire pour `lieu` afin de synchroniser lieuDefini
const lieu = computed({
  get: () => state.lieu,
  set: (v) => { state.lieu = v; state.lieuDefini = !!v },
})

// Validation coordonnées
const coordonneesErrors = reactive({ email: null as string | null, telephone: null as string | null })

function validateCoordonneesField(field: 'email' | 'telephone') {
  coordonneesErrors[field] = null
  if (field === 'email' && state.email && !validateEmail(state.email)) {
    coordonneesErrors.email = t('tunnel.etape6.error-email')
  }
  if (field === 'telephone' && state.telephone && !validatePhone(state.telephone)) {
    coordonneesErrors.telephone = t('tunnel.etape6.error-phone')
  }
}

const coordonneesValidateDisabled = computed(
  () =>
    (!!state.email && !validateEmail(state.email)) ||
    (!!state.telephone && !validatePhone(state.telephone)),
)

// ── Submit ────────────────────────────────────────────────────────────────────

type AnyFieldKey = IndividualFieldKey | 'ville' | 'lieu' | 'nbInvites' | 'prenom' | 'nom' | 'email' | 'telephone'

const FIELD_TO_ITEM: Record<string, string> = {
  ville: 'detailsPratiques',
  lieu: 'detailsPratiques',
  nbInvites: 'detailsPratiques',
  prenom: 'coordonnees',
  nom: 'coordonnees',
  email: 'coordonnees',
  telephone: 'coordonnees',
}

const REQUIRED_FIELDS: AnyFieldKey[] = [
  'eventType', 'ambiance', 'momentCle', 'ville', 'prenom', 'nom', 'email', 'telephone',
]

function isFieldValid(key: AnyFieldKey): boolean {
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
  const missing = REQUIRED_FIELDS.find((k) => !isFieldValid(k))
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

  &:hover { color: $text-primary; }
}

// ─── Liste ────────────────────────────────────────────────────────────────────
.recap-list {
  flex: 1;
  overflow-y: auto;
  overscroll-behavior: contain;
}

@keyframes error-pulse {
  0%, 100% { background-color: transparent; }
  25%, 75% { background-color: rgba($state-error, 0.07); }
}

.recap-item {
  padding: $spacing-m;
  border-bottom: 1px solid $divider-color;

  &.highlighted { animation: error-pulse 2.5s ease; }

  // ── Item individuel ──────────────────────────────────────────────────────────
  &:not(.recap-item--group) {
    display: flex;
    align-items: center;
    gap: $spacing-s;
  }

  // ── Item groupé ──────────────────────────────────────────────────────────────
  &--group {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
  }
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

    &::before { content: '⚠ '; }
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
  transition: border-color 150ms ease, color 150ms ease;

  &:hover {
    border-color: $brand-accent;
    color: $brand-accent;
  }
}

// ─── Groupe header + subfields ────────────────────────────────────────────────
.group-header {
  display: flex;
  align-items: center;
  gap: $spacing-s;
}

.group-label {
  font-size: 0.78rem;
  font-weight: 600;
  color: $text-secondary;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  flex: 1;
}

.group-subfields {
  display: flex;
  flex-direction: column;
  gap: $spacing-xxs;
  padding-left: calc(1.5rem + $spacing-s); // aligne sous l'emoji du group-header
}

.subfield {
  display: flex;
  align-items: baseline;
  gap: $spacing-s;
  font-size: 0.88rem;
}

.subfield-label {
  color: $text-secondary;
  min-width: 6rem;
  flex-shrink: 0;

  &.is-required-missing {
    color: $state-error;
    font-weight: 500;
  }
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

  &.is-empty { color: $text-secondary; }
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

// ─── Sheet individuelle (options) ─────────────────────────────────────────────
.sheet-option-body { padding: $spacing-m; }

.sheet-field-body {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m;
}

.validate-btn { align-self: flex-end; }

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

  &:focus { border-color: $brand-accent; }

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
