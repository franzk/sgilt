<template>
  <section class="event-block">
    <!-- ── Header : titre + crayon / annuler ────────────────────────────────── -->
    <div class="event-block__header">
      <h1 v-if="!editMode" class="event-block__title">{{ event.title }}</h1>
      <input
        v-else
        v-model="draft.title"
        class="event-block__title-input"
        type="text"
        placeholder="Titre de l'événement"
      />

      <button
        v-if="!editMode"
        class="event-block__edit-btn"
        type="button"
        aria-label="Modifier l'événement"
        @click="enterEditMode"
      >
        <IconEditNote />
      </button>
      <button v-else class="event-block__cancel-btn" type="button" @click="handleCancel">
        Annuler
      </button>
    </div>

    <!-- ── Pills (date fixe toujours, autres selon mode) ────────────────────── -->
    <div class="event-pills">
      <span v-if="event.date" class="event-pill event-pill--fixed">
        📅 {{ formatDate(event.date) }}
      </span>

      <!-- Mode lecture -->
      <template v-if="!editMode">
        <span v-if="eventTypeLabel" class="event-pill"
          >{{ eventTypeEmoji }} {{ eventTypeLabel }}</span
        >
        <span v-if="ambianceLabel" class="event-pill">{{ ambianceEmoji }} {{ ambianceLabel }}</span>
        <span v-if="event.ville" class="event-pill">📍 {{ event.ville }}</span>
        <span v-if="event.nbInvites" class="event-pill">👥 {{ event.nbInvites }} invités</span>
      </template>
    </div>

    <!-- ── Champs édition ────────────────────────────────────────────────────── -->
    <template v-if="editMode">
      <div class="edit-fields">
        <div class="edit-field">
          <label class="edit-field__label">Type d'événement</label>
          <select v-model="draft.eventType" class="edit-field__select">
            <option value="">— Non renseigné</option>
            <option v-for="o in EVENT_TYPE_OPTIONS" :key="o.value" :value="o.value">
              {{ o.emoji }} {{ o.label }}
            </option>
          </select>
        </div>

        <div class="edit-field">
          <label class="edit-field__label">Ambiance</label>
          <select v-model="draft.ambiance" class="edit-field__select">
            <option value="">— Non renseigné</option>
            <option v-for="o in AMBIANCE_OPTIONS" :key="o.value" :value="o.value">
              {{ o.emoji }} {{ o.label }}
            </option>
          </select>
        </div>

        <div class="edit-field">
          <label class="edit-field__label">Ville</label>
          <input v-model="draft.ville" class="edit-field__input" type="text" placeholder="Ville" />
        </div>

        <div class="edit-field">
          <label class="edit-field__label">Nombre d'invités</label>
          <input
            v-model="draft.nbInvites"
            class="edit-field__input"
            type="number"
            min="0"
            placeholder="0"
          />
        </div>
      </div>
    </template>

    <!-- ── Note partagée ────────────────────────────────────────────────────── -->
    <div class="event-note">
      <span class="event-note__label">Note partagée avec les prestataires</span>

      <!-- Lecture -->
      <p
        v-if="!editMode"
        class="event-note__text"
        :class="{ 'event-note__text--empty': !event.sharedNote }"
      >
        {{ event.sharedNote || 'Aucune note pour le moment.' }}
      </p>

      <button
        v-if="!editMode && lastUpdateDate"
        class="event-note__journal-btn"
        type="button"
        @click="journalOpen = true"
      >
        {{ lastUpdateDate }}
      </button>

      <!-- Édition -->
      <textarea
        v-else
        ref="noteRef"
        v-model="draft.sharedNote"
        class="event-note__textarea"
        placeholder="Informations utiles pour tous vos prestataires : adresse du lieu, horaires, consignes particulières…"
        rows="3"
        @input="autoResize"
      />
    </div>

    <!-- ── Bouton Enregistrer ────────────────────────────────────────────────── -->
    <SgiltButton v-if="editMode" :disabled="saving" @click="save">
      {{ saving ? 'Enregistrement…' : 'Enregistrer' }}
    </SgiltButton>
  </section>

  <!-- ── Journal ─────────────────────────────────────────────────────────────── -->
  <EventJournal v-model:open="journalOpen" :journal="event.journal ?? []" />

  <!-- ── Modale abandon ───────────────────────────────────────────────────────── -->
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
          <h3 class="abandon-modal__title">Abandonner les modifications&nbsp;?</h3>
          <p class="abandon-modal__body">Vos changements ne seront pas sauvegardés.</p>
          <div class="abandon-modal__actions">
            <SgiltButton variant="secondary" @click="showAbandonModal = false">
              Continuer à éditer
            </SgiltButton>
            <button class="abandon-modal__destructive" type="button" @click="confirmAbandon">
              Abandonner
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import IconEditNote from '~/components/icons/IconEditNote.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { EventMockService } from '~/services/event.mock'
import type { EventDetail } from '~/types/event'
import { EVENT_TYPE_OPTIONS, AMBIANCE_OPTIONS } from '~/types/demande'
import EventJournal from '~/components/app/EventJournal.vue'

const props = defineProps<{ event: EventDetail }>()
const emit = defineEmits<{ updated: [patch: Partial<EventDetail>] }>()

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

// ── Mode édition ──────────────────────────────────────────────────────────────
const editMode = ref(false)
const saving = ref(false)
const showAbandonModal = ref(false)

const draft = reactive({
  title: '',
  eventType: '',
  ambiance: '',
  ville: '',
  nbInvites: '',
  sharedNote: '',
})

function enterEditMode() {
  draft.title = props.event.title
  draft.eventType = props.event.eventType ?? ''
  draft.ambiance = props.event.ambiance ?? ''
  draft.ville = props.event.ville ?? ''
  draft.nbInvites = props.event.nbInvites ?? ''
  draft.sharedNote = props.event.sharedNote
  editMode.value = true
}

const isDirty = computed(
  () =>
    draft.title !== props.event.title ||
    draft.eventType !== (props.event.eventType ?? '') ||
    draft.ambiance !== (props.event.ambiance ?? '') ||
    draft.ville !== (props.event.ville ?? '') ||
    draft.nbInvites !== (props.event.nbInvites ?? '') ||
    draft.sharedNote !== props.event.sharedNote,
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
  const patch = {
    title: draft.title,
    eventType: draft.eventType || undefined,
    ambiance: draft.ambiance || undefined,
    ville: draft.ville || undefined,
    nbInvites: draft.nbInvites || undefined,
    sharedNote: draft.sharedNote,
  }
  await EventMockService.patchEvent(props.event.id, patch)
  emit('updated', patch)
  saving.value = false
  editMode.value = false
}

// ── Labels lecture ─────────────────────────────────────────────────────────
const eventTypeOpt = computed(() =>
  EVENT_TYPE_OPTIONS.find((o) => o.value === props.event.eventType),
)
const eventTypeLabel = computed(() => eventTypeOpt.value?.label ?? null)
const eventTypeEmoji = computed(() => eventTypeOpt.value?.emoji ?? '')

const ambianceOpt = computed(() => AMBIANCE_OPTIONS.find((o) => o.value === props.event.ambiance))
const ambianceLabel = computed(() => ambianceOpt.value?.label ?? null)
const ambianceEmoji = computed(() => ambianceOpt.value?.emoji ?? '')

// ── Auto-resize textarea ──────────────────────────────────────────────────────
const noteRef = ref<HTMLTextAreaElement | null>(null)

function autoResize() {
  const el = noteRef.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = `${el.scrollHeight}px`
}

watch(editMode, (on) => {
  if (on) nextTick(autoResize)
})

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
// ── Bloc ──────────────────────────────────────────────────────────────────────
.event-block {
  background: #fff;
  border-radius: $border-radius-s;
  padding: $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  box-shadow: 0 1px 4px $shadow-s;

  &__header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: $spacing-s;
  }

  &__title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.5rem;
    font-weight: 600;
    color: $brand-primary;
    margin: 0;
    line-height: 1.2;
    flex: 1;
  }

  &__title-input {
    flex: 1;
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.4rem;
    font-weight: 600;
    color: $brand-primary;
    border: 1px solid $divider-color;
    border-radius: $radius-sm;
    padding: 4px $spacing-xs;
    background: $surface-soft;
    outline: none;
    min-width: 0;

    &:focus {
      border-color: $input-focus-border-color;
      box-shadow: $input-focus-box-shadow;
      background: #fff;
    }
  }

  &__edit-btn {
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

  &__cancel-btn {
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
}

// ── Pills ─────────────────────────────────────────────────────────────────────
.event-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.event-pill {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 3px 8px;
  border-radius: 2rem;
  background: $surface-soft;
  border: 1px solid $divider-color;
  font-size: 0.688rem; // ~11px
  color: $text-secondary;
  font-weight: 500;
  white-space: nowrap;

  &--fixed {
    background: rgba($brand-accent, 0.08);
    border-color: rgba($brand-accent, 0.3);
    color: darken(#e6b800, 18%);
  }
}

// ── Champs édition ────────────────────────────────────────────────────────────
.edit-fields {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.edit-field {
  display: flex;
  flex-direction: column;
  gap: 4px;

  &__label {
    font-size: 0.75rem;
    font-weight: 600;
    color: $text-secondary;
    letter-spacing: 0.02em;
  }

  &__input,
  &__select {
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
  }

  &__select {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath d='M1 1l5 5 5-5' stroke='%236b635c' stroke-width='1.5' fill='none' stroke-linecap='round'/%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: right $spacing-s center;
    padding-right: $spacing-xl;
  }
}

// ── Note ──────────────────────────────────────────────────────────────────────
.event-note {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  &__label {
    font-size: 0.75rem;
    font-weight: 600;
    color: $text-secondary;
    letter-spacing: 0.02em;
  }

  &__text {
    font-style: italic;
    font-size: 0.9rem;
    color: $brand-muted;
    line-height: 1.6;
    margin: 0;
    white-space: pre-wrap;

    &--empty {
      opacity: 0.45;
    }
  }

  &__journal-btn {
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

  &__textarea {
    width: 100%;
    padding: $spacing-xs $spacing-s;
    border: 1px solid $divider-color;
    border-radius: $radius-sm;
    font-family: inherit;
    font-size: 0.9rem;
    color: $text-primary;
    line-height: 1.6;
    resize: none;
    background: $surface-soft;
    outline: none;
    box-sizing: border-box;
    overflow: hidden;
    transition:
      border-color 150ms ease,
      box-shadow 150ms ease;

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

  &__title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.2rem;
    font-weight: 600;
    color: $brand-primary;
    margin: 0;
  }

  &__body {
    font-size: 0.9rem;
    color: $text-secondary;
    margin: 0;
    line-height: 1.5;
  }

  &__actions {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  &__destructive {
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
