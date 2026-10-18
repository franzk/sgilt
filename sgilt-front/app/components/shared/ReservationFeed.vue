<template>
  <div class="feed">
    <!-- ── En-tête ──────────────────────────────────────────────────────────── -->
    <div class="feed__header">
      <h3 class="title">{{ $t('feed.title') }}</h3>
      <p class="subtitle">{{ $t('feed.subtitle') }}</p>
    </div>

    <!-- ── Actions ────────────────────────────────────────────────────────────── -->
    <div v-if="canAddNote" class="feed__actions">
      <button class="feed__action-btn" type="button" @click="openNoteModal">
        <StickyNoteAddIcon class="icon" aria-hidden="true" />
        {{ $t('feed.actions.add-note') }}
      </button>
      <button
        v-if="canUploadDocument"
        class="feed__action-btn"
        type="button"
        :disabled="uploadingDocument"
        @click="fileInputRef?.click()"
      >
        <span v-if="uploadingDocument" class="feed__upload-spinner" aria-hidden="true" />
        <AttachmentIcon v-else class="icon" aria-hidden="true" />
        {{ uploadingDocument ? $t('feed.actions.uploading') : $t('feed.actions.add-document') }}
      </button>
    </div>

    <p v-if="uploadError" class="feed__upload-error">{{ uploadError }}</p>

    <!-- ── Skeleton ─────────────────────────────────────────────────────────── -->
    <div v-if="loading" class="feed__skeleton">
      <Sk v-for="i in 3" :key="i" height="6rem" radius="1rem" />
    </div>

    <!-- ── Frise : du plus récent au plus ancien ──────────────────────────────── -->
    <ol v-else-if="sortedItems.length > 0" class="feed__timeline">
      <li v-for="item in sortedItems" :key="item.id">
        <FeedEntry
          :item="item"
          :current-user-role="currentUserRole"
          @download="(url, fileName) => $emit('download-document', url, fileName)"
          @delete="(id) => $emit('delete-document', id)"
        />
      </li>
    </ol>

    <p v-else class="feed__empty">{{ $t('feed.empty') }}</p>

    <input ref="fileInputRef" type="file" style="display: none" @change="handleUpload" />

    <!-- ── Modale ajout note ──────────────────────────────────────────────────── -->
    <SgiltDialog
      v-if="noteModalOpen"
      v-model:open="noteModalOpen"
      :title="$t('feed.add-note-dialog.title')"
      max-width="800px"
    >
      <div class="note-form">
        <input
          ref="noteTitreRef"
          v-model="newNoteTitre"
          class="titre-input"
          type="text"
          :placeholder="$t('feed.add-note-dialog.title-placeholder')"
        />
        <textarea
          ref="noteTextareaRef"
          v-model="newNote"
          class="textarea"
          :placeholder="$t('feed.add-note-dialog.placeholder')"
          rows="5"
          @input="autoResize"
        />
        <div class="note-form__footer">
          <p v-if="noteError" class="error" role="alert">{{ $t('feed.add-note-dialog.error') }}</p>
          <label v-if="showPersonalToggle" class="personal">
            <input v-model="noteIsPersonal" type="checkbox" />
            <span>{{ $t('feed.add-note-dialog.private-toggle') }}</span>
          </label>
          <SgiltButton
            :disabled="!newNote.trim() || !newNoteTitre.trim()"
            :loading="addingNote"
            @click="sendNote"
          >
            {{ $t('feed.add-note-dialog.submit') }}
          </SgiltButton>
        </div>
      </div>
    </SgiltDialog>
  </div>
</template>

<script setup lang="ts">
import { AttachmentIcon, StickyNoteAddIcon } from '@remixicons/vue/line'
import type { FeedItem } from '~/types/event'
import FeedEntry from '~/components/shared/FeedEntry.vue'
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import Sk from '~/components/basics/Sk.vue'

const props = withDefaults(
  defineProps<{
    items: FeedItem[]
    currentUserRole: 'client' | 'prestataire'
    loading?: boolean
    canAddNote?: boolean
    canUploadDocument?: boolean
    uploadingDocument?: boolean
    // Note en cours d'enregistrement : la fenêtre reste ouverte, bouton en chargement.
    addingNote?: boolean
    // Échec du dernier enregistrement : la fenêtre reste ouverte avec un message.
    addNoteFailed?: boolean
    showPersonalToggle?: boolean
  }>(),
  {
    loading: false,
    canAddNote: false,
    canUploadDocument: false,
    uploadingDocument: false,
    addingNote: false,
    addNoteFailed: false,
    showPersonalToggle: false,
  },
)

const emit = defineEmits<{
  'add-note': [title: string, content: string, isPersonal: boolean]
  'upload-document': [file: File]
  'download-document': [url: string, fileName: string]
  'delete-document': [id: string]
}>()

const { t } = useI18n()

// ── Frise : du plus récent au plus ancien ──────────────────────────────────────
const sortedItems = computed(() =>
  [...props.items].sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime()),
)

// ── Documents ─────────────────────────────────────────────────────────────────
const fileInputRef = ref<HTMLInputElement | null>(null)

const uploadError = ref<string | null>(null)
const MAX_FILE_SIZE = 10 * 1024 * 1024 // 10 MB

function handleUpload(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file || !file.size) return
  ;(e.target as HTMLInputElement).value = ''
  if (file.size > MAX_FILE_SIZE) {
    uploadError.value = t('feed.upload-error.too-large')
    return
  }
  uploadError.value = null
  emit('upload-document', file)
}

// ── Modale note ────────────────────────────────────────────────────────────────
const noteModalOpen = ref(false)
const newNote = ref('')
const newNoteTitre = ref('')
const noteIsPersonal = ref(false)
// Note envoyée, en attente de la fin de son enregistrement (addingNote) : fermeture si elle a réussi,
// message d'erreur sinon (la saisie est gardée pour réessayer).
const awaitingNote = ref(false)
const noteError = ref(false)
const noteTitreRef = ref<HTMLInputElement | null>(null)
const noteTextareaRef = ref<HTMLTextAreaElement | null>(null)

function openNoteModal() {
  noteError.value = false
  newNote.value = ''
  newNoteTitre.value = ''
  noteIsPersonal.value = false
  noteModalOpen.value = true
}

watch(noteModalOpen, (val) => {
  if (val) nextTick(() => noteTitreRef.value?.focus())
})

function autoResize(e: Event) {
  const el = e.target as HTMLTextAreaElement
  el.style.height = 'auto'
  el.style.height = `${el.scrollHeight}px`
}

function sendNote() {
  const title = newNoteTitre.value.trim()
  const content = newNote.value.trim()
  if (!title || !content || props.addingNote) return
  noteError.value = false
  awaitingNote.value = true
  emit('add-note', title, content, noteIsPersonal.value)
}

watch(
  () => props.addingNote,
  (adding) => {
    if (adding || !awaitingNote.value) return
    awaitingNote.value = false
    if (props.addNoteFailed) noteError.value = true
    else noteModalOpen.value = false
  },
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.feed {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  // ── Skeleton ───────────────────────────────────────────────────────────────
  &__skeleton {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
  }

  // ── En-tête ────────────────────────────────────────────────────────────────
  &__header {
    display: flex;
    flex-direction: column;
    gap: $spacing-xxs;

    .title {
      margin: 0;
      font-family: 'Cormorant Garamond', serif;
      font-size: $font-size-2xl;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      line-height: 1.1;
    }

    .subtitle {
      margin: 0;
      font-size: $font-size-sm;
      color: $text-secondary;
      line-height: 1.5;
    }
  }

  // ── Actions ────────────────────────────────────────────────────────────────
  &__actions {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: $spacing-s;
  }

  &__action-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-xs;
    padding: $spacing-s $spacing-m;
    border: 1px solid $divider-color;
    border-radius: 9999px;
    background: $surface-white;
    color: $text-primary;
    font-family: inherit;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    cursor: pointer;
    transition: border-color 150ms ease;

    &:hover {
      border-color: $brand-primary;
    }

    .icon {
      width: 1.125rem;
      height: 1.125rem;
    }
  }

  &__upload-spinner {
    display: inline-block;
    width: 0.75rem;
    height: 0.75rem;
    border: 2px solid currentColor;
    border-top-color: transparent;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }

  &__upload-error {
    margin: 0;
    font-size: $font-size-sm;
    color: $state-error;
  }

  // ── Frise ──────────────────────────────────────────────────────────────────
  // Une ligne verticale à gauche des cartes ; chaque carte (FeedEntry) pose son point dessus, de la
  // couleur de sa pastille, dans la marge laissée ici ($spacing-l).
  &__timeline {
    position: relative;
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
    margin: 0;
    padding: 0 0 0 $spacing-l;
    list-style: none;

    &::before {
      content: '';
      position: absolute;
      top: $spacing-m;
      bottom: $spacing-m;
      left: 0.3rem;
      width: 2px;
      background: $divider-color;
    }

  }

  &__empty {
    margin: 0;
    padding: $spacing-m 0;
    font-size: $font-size-sm;
    font-style: italic;
    color: $text-secondary;
    text-align: center;
  }
}

// ── Modale note ────────────────────────────────────────────────────────────────
.note-form {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  padding: 32px;
  gap: $spacing-m;

  .titre-input {
    width: 100%;
    padding: 0;
    border: none;
    border-bottom: 1px solid $divider-color;
    font-family: inherit;
    font-size: 1.1rem;
    font-weight: 700;
    color: $text-primary;
    background: transparent;
    outline: none;
    padding-bottom: $spacing-xs;

    &::placeholder {
      color: $text-secondary;
      opacity: 0.35;
      font-weight: 400;
    }
  }

  .textarea {
    flex: 1;
    min-height: 80px;
    width: 100%;
    padding: 0;
    border: none;
    resize: none;
    font-family: inherit;
    font-size: 1rem;
    line-height: 1.8;
    color: $text-primary;
    background: transparent;
    outline: none;
    overflow-y: auto;

    &::placeholder {
      color: $text-secondary;
      opacity: 0.4;
    }
  }

  &__footer {
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
    padding-bottom: var(--keyboard-offset, 0px);
    transition: padding-bottom 120ms ease;
  }

  .error {
    margin: 0;
    font-size: $font-size-sm;
    color: $state-error;
  }

  .personal {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    color: $text-secondary;
    cursor: pointer;
  }
}
</style>
