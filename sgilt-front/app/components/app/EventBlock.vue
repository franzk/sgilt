<template>
  <section class="event-block">
    <!-- ── Toggle accordéon : pills + actions ──────────────────────────────────── -->
    <button class="toggle" type="button" @click="open = !open">
      <div class="pills">
        <span v-if="event.date" class="event-pill date">
          <CalendarEventIcon class="icon" />{{ formatDate(event.date) }}
        </span>
        <span v-if="event.ville" class="event-pill">
          <MapPin2Icon class="icon" />{{ event.ville }}
        </span>
        <span v-if="event.nbInvites" class="event-pill">
          <GroupIcon class="icon" />{{ event.nbInvites }}
        </span>
      </div>
      <ArrowUpSIcon v-if="open" class="chevron" />
      <ArrowDownSIcon v-else class="chevron" />
    </button>

    <!-- ── Corps accordéon ─────────────────────────────────────────────────────── -->
    <div v-if="open" class="body">
      <!-- 1. LOGISTIQUE -->
      <div class="section">
        <span class="section-label">Logistique</span>
        <p v-if="!editMode" class="field-value" :class="{ empty: !event.lieu }">
          {{ event.lieu || 'Salle, adresse… non renseigné' }}
        </p>
        <input
          v-else
          v-model="draft.lieu"
          class="input"
          type="text"
          placeholder="Salle, adresse…"
        />
      </div>

      <hr class="divider" />

      <!-- 2. NOTE PARTAGÉE -->
      <div class="section">
        <span class="section-label">Note partagée</span>
        <p v-if="!editMode" class="note-text" :class="{ empty: !event.sharedNote }">
          {{ event.sharedNote || 'Aucune note pour le moment.' }}
        </p>
        <textarea
          v-else
          ref="noteRef"
          v-model="draft.sharedNote"
          class="textarea"
          placeholder="Informations utiles pour tous vos prestataires…"
          rows="3"
          @input="autoResize"
        />
      </div>

      <hr class="divider" />

      <!-- 3. COORDONNÉES -->
      <div class="section">
        <span class="section-label">Coordonnées</span>

        <!-- Variant pro : lecture + boutons copier -->
        <template v-if="variant === 'pro'">
          <span class="contact-name">{{ clientInfo.firstName }}</span>
          <div class="contact-row">
            <span class="contact-value">{{ clientInfo.phone }}</span>
            <button
              class="copy-btn"
              type="button"
              :class="{ copied: phoneCopied }"
              :aria-label="phoneCopied ? 'Copié' : 'Copier le numéro'"
              @click="copyPhone"
            >
              <CheckIcon v-if="phoneCopied" class="copy-icon" />
              <FileCopyIcon v-else class="copy-icon" />
            </button>
          </div>
          <div class="contact-row">
            <span class="contact-value email">{{ clientInfo.email }}</span>
            <button
              class="copy-btn"
              type="button"
              :class="{ copied: emailCopied }"
              :aria-label="emailCopied ? 'Copié' : 'Copier l\'email'"
              @click="copyEmail"
            >
              <CheckIcon v-if="emailCopied" class="copy-icon" />
              <FileCopyIcon v-else class="copy-icon" />
            </button>
          </div>
        </template>

        <!-- Variant client : lecture ou édition -->
        <template v-else>
          <template v-if="!editMode">
            <span class="contact-name">
              {{ clientInfo.firstName }}{{ clientInfo.lastName ? ' ' + clientInfo.lastName : '' }}
            </span>
            <span class="contact-value">{{ clientInfo.phone }}</span>
            <span class="contact-value email">{{ clientInfo.email }}</span>
          </template>
          <template v-else>
            <div class="edit-field">
              <label class="label">Prénom</label>
              <input v-model="draft.firstName" class="input" type="text" />
            </div>
            <div class="edit-field">
              <label class="label">Nom</label>
              <input v-model="draft.lastName" class="input" type="text" />
            </div>
            <div class="edit-field">
              <label class="label">Téléphone</label>
              <input v-model="draft.phone" class="input" type="tel" />
            </div>
            <div class="edit-field">
              <label class="label">Email</label>
              <input v-model="draft.email" class="input" type="email" />
            </div>
          </template>
        </template>
      </div>

      <hr class="divider" />

      <!-- 4. LA FÊTE -->
      <div class="section">
        <span class="section-label">La fête</span>

        <!-- Lecture -->
        <template v-if="!editMode">
          <dl v-if="eventTypeLabel || ambianceLabel" class="brief-fields">
            <template v-if="eventTypeLabel">
              <dt>Type</dt>
              <dd>{{ eventTypeEmoji }} {{ eventTypeLabel }}</dd>
            </template>
            <template v-if="ambianceLabel">
              <dt>Ambiance</dt>
              <dd>{{ ambianceEmoji }} {{ ambianceLabel }}</dd>
            </template>
            <dt>Moment clé</dt>
            <dd class="empty">Non renseigné</dd>
          </dl>
          <p v-else class="field-value empty">Non renseigné</p>
        </template>

        <!-- Édition (client uniquement) -->
        <template v-else-if="variant === 'client'">
          <div class="edit-fields">
            <div class="edit-field">
              <label class="label">Type d'événement</label>
              <select v-model="draft.eventType" class="select">
                <option value="">— Non renseigné</option>
                <option v-for="o in EVENT_TYPE_OPTIONS" :key="o.value" :value="o.value">
                  {{ o.emoji }} {{ o.label }}
                </option>
              </select>
            </div>
            <div class="edit-field">
              <label class="label">Ambiance</label>
              <select v-model="draft.ambiance" class="select">
                <option value="">— Non renseigné</option>
                <option v-for="o in AMBIANCE_OPTIONS" :key="o.value" :value="o.value">
                  {{ o.emoji }} {{ o.label }}
                </option>
              </select>
            </div>
            <div class="edit-field">
              <label class="label">Ville</label>
              <input v-model="draft.ville" class="input" type="text" placeholder="Ville" />
            </div>
            <div class="edit-field">
              <label class="label">Nombre d'invités</label>
              <input
                v-model="draft.nbInvites"
                class="input"
                type="number"
                min="0"
                placeholder="0"
              />
            </div>
          </div>
        </template>
      </div>

      <hr class="divider" />

      <!-- 5. MISE À JOUR -->
      <div class="section">
        <div class="update-row">
          <span class="section-label">Mise à jour</span>
          <template v-if="variant === 'client'">
            <button
              v-if="!editMode"
              class="edit-btn"
              type="button"
              aria-label="Modifier l'événement"
              @click="enterEditMode"
            >
              <IconEditNote />
            </button>
            <button v-else class="cancel-btn" type="button" @click="handleCancel">Annuler</button>
          </template>
        </div>
        <div class="update-date-row">
          <button
            v-if="lastUpdateDate"
            class="journal-btn"
            type="button"
            @click="journalOpen = true"
          >
            {{ lastUpdateDate }}
          </button>
          <p v-else class="field-value empty">Aucune modification enregistrée.</p>
          <button v-if="editMode" class="save-btn" type="button" :disabled="saving" @click="save">
            {{ saving ? 'Enregistrement…' : 'Enregistrer' }}
          </button>
        </div>
      </div>
    </div>
  </section>

  <!-- ── Journal ─────────────────────────────────────────────────────────────────── -->
  <EventJournal v-model:open="journalOpen" :journal="event.journal ?? []" />

  <!-- ── Modale abandon ───────────────────────────────────────────────────────────── -->
  <Teleport to="body">
    <Transition name="modal">
      <div
        v-if="showAbandonModal"
        class="abandon-overlay"
        role="dialog"
        aria-modal="true"
        @click.self="showAbandonModal = false"
      >
        <div class="abandon-modal">
          <h3 class="title">Abandonner les modifications&nbsp;?</h3>
          <p class="body">Vos changements ne seront pas sauvegardés.</p>
          <div class="actions">
            <SgiltButton variant="secondary" @click="showAbandonModal = false">
              Continuer à éditer
            </SgiltButton>
            <button class="destructive" type="button" @click="confirmAbandon">Abandonner</button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import IconEditNote from '~/components/icons/IconEditNote.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import EventJournal from '~/components/app/EventJournal.vue'
import { EventMockService } from '~/services/event.mock'
import type { EventDetail, ClientContactInfo } from '~/types/event'
import { EVENT_TYPE_OPTIONS, AMBIANCE_OPTIONS } from '~/types/demande'
import {
  ArrowDownSIcon,
  ArrowUpSIcon,
  FileCopyIcon,
  CalendarEventIcon,
  MapPin2Icon,
  GroupIcon,
  CheckIcon,
} from '@remixicons/vue/line'

const props = defineProps<{
  event: EventDetail
  clientInfo: ClientContactInfo
  variant?: 'client' | 'pro'
}>()

const emit = defineEmits<{
  updated: [patch: Partial<EventDetail>]
  updatedClientInfo: [patch: Partial<ClientContactInfo>]
}>()

const variant = computed(() => props.variant ?? 'client')

// ── Accordéon ─────────────────────────────────────────────────────────────────
const open = ref(false)

// ── Journal ───────────────────────────────────────────────────────────────────
const journalOpen = ref(false)

const lastUpdateDate = computed(() => {
  const journal = props.event.journal
  if (!journal?.length) return null
  const last = journal[journal.length - 1]
  return last?.date
    ? `Dernière mise à jour : ${new Date(last.date).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' })}`
    : null
})

// ── Labels lecture ─────────────────────────────────────────────────────────────
const eventTypeOpt = computed(() =>
  EVENT_TYPE_OPTIONS.find((o) => o.value === props.event.eventType),
)
const eventTypeLabel = computed(() => eventTypeOpt.value?.label ?? null)
const eventTypeEmoji = computed(() => eventTypeOpt.value?.emoji ?? '')

const ambianceOpt = computed(() => AMBIANCE_OPTIONS.find((o) => o.value === props.event.ambiance))
const ambianceLabel = computed(() => ambianceOpt.value?.label ?? null)
const ambianceEmoji = computed(() => ambianceOpt.value?.emoji ?? '')

// ── Copier (variant pro) ───────────────────────────────────────────────────────
const phoneCopied = ref(false)
const emailCopied = ref(false)

async function copyPhone() {
  await navigator.clipboard.writeText(props.clientInfo.phone)
  phoneCopied.value = true
  setTimeout(() => (phoneCopied.value = false), 2000)
}

async function copyEmail() {
  await navigator.clipboard.writeText(props.clientInfo.email)
  emailCopied.value = true
  setTimeout(() => (emailCopied.value = false), 2000)
}

// ── Mode édition ──────────────────────────────────────────────────────────────
const editMode = ref(false)
const saving = ref(false)
const showAbandonModal = ref(false)

const draft = reactive({
  eventType: '',
  ambiance: '',
  ville: '',
  lieu: '',
  nbInvites: '',
  sharedNote: '',
  firstName: '',
  lastName: '',
  phone: '',
  email: '',
})

function enterEditMode() {
  draft.eventType = props.event.eventType ?? ''
  draft.ambiance = props.event.ambiance ?? ''
  draft.ville = props.event.ville ?? ''
  draft.lieu = props.event.lieu ?? ''
  draft.nbInvites = props.event.nbInvites ?? ''
  draft.sharedNote = props.event.sharedNote
  draft.firstName = props.clientInfo.firstName
  draft.lastName = props.clientInfo.lastName ?? ''
  draft.phone = props.clientInfo.phone
  draft.email = props.clientInfo.email
  editMode.value = true
  open.value = true
  nextTick(autoResize)
}

const isDirty = computed(
  () =>
    draft.eventType !== (props.event.eventType ?? '') ||
    draft.ambiance !== (props.event.ambiance ?? '') ||
    draft.ville !== (props.event.ville ?? '') ||
    draft.lieu !== (props.event.lieu ?? '') ||
    draft.nbInvites !== (props.event.nbInvites ?? '') ||
    draft.sharedNote !== props.event.sharedNote ||
    draft.firstName !== props.clientInfo.firstName ||
    draft.lastName !== (props.clientInfo.lastName ?? '') ||
    draft.phone !== props.clientInfo.phone ||
    draft.email !== props.clientInfo.email,
)

function handleCancel() {
  if (isDirty.value) {
    showAbandonModal.value = true
  } else {
    editMode.value = false
  }
}

function confirmAbandon() {
  showAbandonModal.value = false
  editMode.value = false
}

async function save() {
  saving.value = true
  const eventPatch: Partial<EventDetail> = {
    eventType: draft.eventType || undefined,
    ambiance: draft.ambiance || undefined,
    ville: draft.ville || undefined,
    lieu: draft.lieu || undefined,
    nbInvites: draft.nbInvites || undefined,
    sharedNote: draft.sharedNote,
  }
  await EventMockService.patchEvent(props.event.id, eventPatch)
  emit('updated', eventPatch)

  const clientPatch: Partial<ClientContactInfo> = {
    firstName: draft.firstName,
    lastName: draft.lastName || undefined,
    phone: draft.phone,
    email: draft.email,
  }
  emit('updatedClientInfo', clientPatch)

  saving.value = false
  editMode.value = false
}

// ── Auto-resize textarea ──────────────────────────────────────────────────────
const noteRef = ref<HTMLTextAreaElement | null>(null)

function autoResize() {
  const el = noteRef.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = `${el.scrollHeight}px`
}

// ── Formatage ─────────────────────────────────────────────────────────────────
function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ── Bloc principal ─────────────────────────────────────────────────────────────

.event-block {
  background: #fff;
  border-radius: $radius-md;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  .toggle {
    width: 100%;
    display: flex;
    align-items: center;
    gap: $spacing-s;
    padding: $spacing-s $spacing-m;
    background: none;
    border: none;
    cursor: pointer;
    text-align: left;
    transition: background 120ms ease;

    &:hover {
      background: $surface-soft;
    }
  }

  .pills {
    flex: 1;
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    min-width: 0;
  }

  .chevron {
    flex-shrink: 0;
    width: 16px;
    height: 16px;
    color: $text-secondary;
  }

  .body {
    display: flex;
    flex-direction: column;
    border-top: 1px solid $divider-color;
  }

  .section {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
    padding: $spacing-s $spacing-m;
  }

  .section-label {
    font-family: 'Inter', sans-serif;
    font-size: 0.65rem;
    font-weight: 600;
    letter-spacing: 0.09em;
    text-transform: uppercase;
    color: $text-secondary;
  }

  .divider {
    border: none;
    border-top: 1px solid $divider-color;
    margin: 0;
  }

  .update-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .update-date-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-s;
  }

  .field-value {
    font-family: 'Inter', sans-serif;
    font-size: 0.85rem;
    color: $text-primary;
    margin: 0;

    &.empty {
      color: $text-secondary;
      opacity: 0.55;
      font-style: italic;
    }
  }

  .note-text {
    font-family: 'Inter', sans-serif;
    font-size: 0.85rem;
    color: $text-primary;
    line-height: 1.55;
    margin: 0;
    white-space: pre-wrap;

    &.empty {
      color: $text-secondary;
      opacity: 0.45;
      font-style: italic;
    }
  }

  .journal-btn {
    align-self: flex-start;
    background: none;
    border: none;
    padding: 0;
    font-family: inherit;
    font-size: 0.75rem;
    font-style: italic;
    color: $text-secondary;
    opacity: 0.6;
    cursor: pointer;
    text-decoration: underline;
    text-underline-offset: 2px;
    transition: opacity 150ms ease;

    &:hover {
      opacity: 1;
    }
  }

  .contact-name {
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    font-weight: 600;
    color: $text-primary;
  }

  .contact-row {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    min-width: 0;
  }

  .contact-value {
    flex: 1;
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-primary;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    &.email {
      font-size: 0.75rem;
      color: $text-secondary;
    }
  }

  .copy-btn {
    flex-shrink: 0;
    width: 22px;
    height: 22px;
    border-radius: $radius-sm;
    border: 1px solid $divider-color;
    background: none;
    color: $text-secondary;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition:
      background 120ms ease,
      color 120ms ease;

    &.copied {
      background: $brand-accent;
      color: $brand-primary;
      border-color: $brand-accent;
    }
  }

  .copy-icon {
    width: 13px;
    height: 13px;
  }

  .edit-btn {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2rem;
    height: 2rem;
    border: none;
    background: none;
    color: $brand-muted;
    border-radius: $radius-sm;
    cursor: pointer;
    transition:
      background 150ms ease,
      color 150ms ease;

    &:hover {
      background: $surface-soft;
      color: $brand-primary;
    }
  }

  .cancel-btn {
    flex-shrink: 0;
    background: none;
    border: none;
    font-family: inherit;
    font-size: 0.85rem;
    color: $brand-muted;
    cursor: pointer;
    padding: 4px 0;
    transition: color 150ms ease;

    &:hover {
      color: $text-primary;
    }
  }

  .save-btn {
    flex-shrink: 0;
    background: none;
    border: 1px solid $divider-color;
    border-radius: $radius-sm;
    font-family: inherit;
    font-size: 0.85rem;
    color: $text-primary;
    cursor: pointer;
    padding: 3px 10px;
    transition:
      border-color 150ms ease,
      color 150ms ease;

    &:hover {
      border-color: $brand-primary;
      color: $brand-primary;
    }
    &:disabled {
      opacity: 0.45;
      cursor: default;
    }
  }

  // ── Champs édition inline (logistique, coordonnées) ──────────────────────────

  .input,
  .textarea {
    width: 100%;
    padding: $spacing-xs $spacing-s;
    border: 1px solid $divider-color;
    border-radius: $radius-sm;
    font-family: inherit;
    font-size: 0.9rem;
    color: $text-primary;
    background: $surface-soft;
    outline: none;
    box-sizing: border-box;
    transition:
      border-color 150ms ease,
      box-shadow 150ms ease;
    appearance: none;

    &:focus {
      border-color: $input-focus-border-color;
      box-shadow: $input-focus-box-shadow;
      background: #fff;
    }

    &::placeholder {
      color: $text-secondary;
      opacity: 0.45;
    }
  }

  .textarea {
    resize: none;
    line-height: 1.6;
    overflow: hidden;
  }
}

// ── Pills ─────────────────────────────────────────────────────────────────────

.event-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  border-radius: 2rem;
  background: $surface-soft;
  border: 1px solid $divider-color;
  font-family: 'Inter', sans-serif;
  font-size: 0.688rem;
  color: $text-secondary;
  font-weight: 500;
  white-space: nowrap;

  .icon {
    width: 11px;
    height: 11px;
    flex-shrink: 0;
  }

  &.date {
    background: rgba($brand-accent, 0.08);
    border-color: rgba($brand-accent, 0.3);
    color: darken(#e6b800, 18%);
  }
}

// ── Champs dt/dd lecture ──────────────────────────────────────────────────────

.brief-fields {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 4px $spacing-s;
  margin: 0;

  dt {
    font-family: 'Inter', sans-serif;
    font-size: 0.7rem;
    font-weight: 600;
    letter-spacing: 0.06em;
    text-transform: uppercase;
    color: $text-secondary;
    align-self: center;
  }

  dd {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-primary;
    margin: 0;
    align-self: center;
  }

  .empty {
    color: $text-secondary;
    opacity: 0.55;
    font-style: italic;
  }
}

// ── Champs édition groupés (section La fête) ──────────────────────────────────

.edit-fields {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.edit-field {
  display: flex;
  flex-direction: column;
  gap: 4px;

  .label {
    font-size: 0.75rem;
    font-weight: 600;
    color: $text-secondary;
    letter-spacing: 0.02em;
  }

  .input,
  .select,
  .textarea {
    width: 100%;
    padding: $spacing-xs $spacing-s;
    border: 1px solid $divider-color;
    border-radius: $radius-sm;
    font-family: inherit;
    font-size: 0.9rem;
    color: $text-primary;
    background: $surface-soft;
    outline: none;
    box-sizing: border-box;
    transition:
      border-color 150ms ease,
      box-shadow 150ms ease;
    appearance: none;

    &:focus {
      border-color: $input-focus-border-color;
      box-shadow: $input-focus-box-shadow;
      background: #fff;
    }

    &::placeholder {
      color: $text-secondary;
      opacity: 0.45;
    }
  }

  .select {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath d='M1 1l5 5 5-5' stroke='%236b635c' stroke-width='1.5' fill='none' stroke-linecap='round'/%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: right $spacing-s center;
    padding-right: $spacing-xl;
  }

  .textarea {
    resize: none;
    line-height: 1.6;
    overflow: hidden;
  }
}

// ── Modale abandon ────────────────────────────────────────────────────────────

.abandon-overlay {
  position: fixed;
  inset: 0;
  z-index: $z-modal;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-m;
}

.abandon-modal {
  background: #fff;
  border-radius: $radius-lg;
  padding: $spacing-l;
  width: 100%;
  max-width: 340px;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.2rem;
    font-weight: 600;
    color: $brand-primary;
    margin: 0;
  }

  .body {
    font-size: 0.9rem;
    color: $text-secondary;
    margin: 0;
    line-height: 1.5;
  }

  .actions {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  .destructive {
    width: 100%;
    padding: $spacing-xs $spacing-m;
    border: none;
    border-radius: $radius-md;
    background: rgba($state-error, 0.1);
    color: $state-error;
    font-family: inherit;
    font-size: 0.95rem;
    font-weight: 600;
    cursor: pointer;
    transition: background 150ms ease;

    &:hover {
      background: rgba($state-error, 0.18);
    }
  }
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 200ms ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
