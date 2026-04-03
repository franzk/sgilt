<template>
  <div class="feed">
    <!-- ── Pills ─────────────────────────────────────────────────────────────── -->
    <div v-if="showPills" class="feed__pills">
      <button
        v-for="pill in PILLS"
        :key="pill.id"
        class="feed__pill"
        :class="{ active: activeFilter === pill.id }"
        type="button"
        @click="activeFilter = pill.id"
      >
        {{ pill.label }}
      </button>
    </div>

    <!-- ── Actions ────────────────────────────────────────────────────────────── -->
    <div v-if="canAddNote" class="feed__actions">
      <button class="feed__action-btn" type="button" @click="openNoteModal">+ Note</button>
      <button
        v-if="canUploadDocument"
        class="feed__action-btn"
        type="button"
        @click="fileInputRef?.click()"
      >
        + Document
      </button>
    </div>

    <!-- ── Flux ─────────────────────────────────────────────────────────────── -->
    <ul v-if="filteredItems.length > 0" class="feed__list">
      <li v-for="item in filteredItems" :key="item.id">
        <!-- Note -->
        <template v-if="item._kind === 'note'">
          <!-- Message initial -->
          <div v-if="item.isMessageInitial" class="note-initial">
            <span class="label">Message initial</span>
            <p class="content">{{ item.content }}</p>
            <span class="meta">
              {{ item.author.name }} · {{ formatDateShort(item.createdAt) }}
            </span>
          </div>

          <!-- Note personnelle -->
          <div v-else-if="item.isPersonal" class="note-card personal">
            <div class="header">
              <span class="lock">🔒</span>
              <span class="author">{{ item.author.name }}</span>
              <span class="date">{{ formatDateShort(item.createdAt) }}</span>
            </div>
            <p class="content">{{ item.content }}</p>
          </div>

          <!-- Note standard -->
          <NoteCard v-else :note="item" />
        </template>

        <!-- Document -->
        <div v-else class="doc-item">
          <div class="icon" :class="item.fileType">
            {{ item.fileType === 'pdf' ? 'PDF' : item.fileType === 'image' ? 'IMG' : 'DOC' }}
          </div>
          <div class="info">
            <span class="name">{{ item.name }}</span>
            <span class="meta">
              {{ item.uploadedBy.name }} · {{ formatDateShort(item.uploadedAt) }}
            </span>
          </div>
          <div class="actions">
            <a :href="item.url" download class="btn" title="Télécharger">↓</a>
            <button
              v-if="canDeleteDoc(item)"
              class="btn delete"
              type="button"
              title="Supprimer"
              @click="$emit('delete-document', item.id)"
            >
              ✕
            </button>
          </div>
        </div>
      </li>
    </ul>

    <p v-else class="feed__empty">Aucun élément pour le moment.</p>

    <input ref="fileInputRef" type="file" style="display: none" @change="handleUpload" />

    <!-- ── Modale ajout note ──────────────────────────────────────────────────── -->
    <SgiltDialog
      v-if="noteModalOpen"
      v-model:open="noteModalOpen"
      title="Ajouter une note"
      max-width="800px"
    >
      <div class="note-form">
        <textarea
          ref="noteTextareaRef"
          v-model="newNote"
          class="textarea"
          placeholder="Ajouter une note..."
          rows="5"
          @input="autoResize"
        />
        <div class="note-form__footer">
          <label v-if="showPersonalToggle" class="personal">
            <input v-model="noteIsPersonal" type="checkbox" />
            <span>Note privée 🔒 (visible uniquement par moi)</span>
          </label>
          <SgiltButton :disabled="!newNote.trim() || sending" @click="sendNote">
            Ajouter la note
          </SgiltButton>
        </div>
      </div>
    </SgiltDialog>
  </div>
</template>

<script setup lang="ts">
import type { FeedItem, FeedDocument } from '~/types/event'
import NoteCard from '~/components/app/NoteCard.vue'
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

const props = withDefaults(
  defineProps<{
    items: FeedItem[]
    canAddNote?: boolean
    canUploadDocument?: boolean
    showPersonalToggle?: boolean
  }>(),
  {
    canAddNote: false,
    canUploadDocument: false,
    showPersonalToggle: false,
  },
)

const emit = defineEmits<{
  'add-note': [content: string, isPersonal: boolean]
  'upload-document': [file: File]
  'delete-document': [id: string]
}>()

// ── Pills ──────────────────────────────────────────────────────────────────────
type FilterId = 'all' | 'notes' | 'documents'
const PILLS: { id: FilterId; label: string }[] = [
  { id: 'all', label: 'Tout' },
  { id: 'notes', label: 'Notes' },
  { id: 'documents', label: 'Documents' },
]
const activeFilter = ref<FilterId>('all')

const hasNotes = computed(() => props.items.some((i) => i._kind === 'note'))
const hasDocuments = computed(() => props.items.some((i) => i._kind === 'document'))
const showPills = computed(() => hasNotes.value && hasDocuments.value)

// ── Flux filtré + trié ─────────────────────────────────────────────────────────
function itemDate(item: FeedItem): number {
  return new Date(item._kind === 'note' ? item.createdAt : item.uploadedAt).getTime()
}

const filteredItems = computed(() => {
  const filtered =
    activeFilter.value === 'all'
      ? props.items
      : props.items.filter(
          (i) => i._kind === (activeFilter.value === 'notes' ? 'note' : 'document'),
        )
  return [...filtered].sort((a, b) => itemDate(b) - itemDate(a))
})

// ── Documents ─────────────────────────────────────────────────────────────────
const fileInputRef = ref<HTMLInputElement | null>(null)

function canDeleteDoc(item: FeedDocument): boolean {
  return item.uploadedBy.role === 'client' || item.uploadedBy.role === 'prestataire'
}

function handleUpload(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  emit('upload-document', file)
  ;(e.target as HTMLInputElement).value = ''
}

// ── Modale note ────────────────────────────────────────────────────────────────
const noteModalOpen = ref(false)
const newNote = ref('')
const noteIsPersonal = ref(false)
const sending = ref(false)
const noteTextareaRef = ref<HTMLTextAreaElement | null>(null)

function openNoteModal() {
  newNote.value = ''
  noteIsPersonal.value = false
  noteModalOpen.value = true
}

watch(noteModalOpen, (val) => {
  if (val) nextTick(() => noteTextareaRef.value?.focus())
})

function autoResize(e: Event) {
  const el = e.target as HTMLTextAreaElement
  el.style.height = 'auto'
  el.style.height = `${el.scrollHeight}px`
}

async function sendNote() {
  const content = newNote.value.trim()
  if (!content || sending.value) return
  sending.value = true
  emit('add-note', content, noteIsPersonal.value)
  sending.value = false
  noteModalOpen.value = false
}

</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.feed {
  display: flex;
  flex-direction: column;
  gap: 1rem;

  // ── Pills ──────────────────────────────────────────────────────────────────
  &__pills {
    display: flex;
    gap: $spacing-xs;
    flex-wrap: nowrap;
  }

  &__pill {
    flex-shrink: 0;
    padding: 5px 14px;
    border-radius: 2rem;
    border: 1px solid $divider-color;
    background: #fff;
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    font-weight: 500;
    color: $text-secondary;
    cursor: pointer;
    transition:
      background 120ms ease,
      color 120ms ease,
      border-color 120ms ease;

    &.active {
      background: $brand-accent;
      color: $brand-primary;
      border-color: $brand-accent;
      font-weight: 600;
    }
  }

  // ── Actions ────────────────────────────────────────────────────────────────
  &__actions {
    display: flex;
    gap: $spacing-xs;
  }

  &__action-btn {
    flex-shrink: 0;
    padding: 5px 14px;
    border: 1px solid $brand-border;
    border-radius: $radius-md;
    background: transparent;
    font-family: inherit;
    font-size: 0.8rem;
    font-weight: 600;
    color: $text-secondary;
    cursor: pointer;
    transition:
      border-color 150ms ease,
      color 150ms ease;

    &:hover {
      border-color: $brand-primary;
      color: $brand-primary;
    }
  }

  // ── Liste flux ─────────────────────────────────────────────────────────────
  &__list {
    list-style: none;
    margin: 0;
    padding: 0;
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
  }

  &__empty {
    font-size: 0.875rem;
    color: $text-secondary;
    opacity: 0.55;
    font-style: italic;
    margin: 0;
    text-align: center;
    padding: $spacing-m 0;
  }
}

// ── Note message initial ───────────────────────────────────────────────────────
.note-initial {
  background: $surface-soft;
  border-radius: $radius-md;
  padding: $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  .label {
    font-family: 'Inter', sans-serif;
    font-size: 0.65rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    color: $brand-muted;
  }

  .content {
    font-family: 'Inter', sans-serif;
    font-size: 0.9rem;
    line-height: 1.6;
    color: $text-primary;
    margin: 0;
    white-space: pre-wrap;
  }

  .meta {
    font-family: 'Inter', sans-serif;
    font-size: 0.72rem;
    color: $text-secondary;
    opacity: 0.7;
  }
}

// ── Note personnelle ───────────────────────────────────────────────────────────
.note-card {
  &.personal {
    background: #fdfaf0;
    border: 1px dashed $brand-accent;
    border-radius: $radius-md;
    padding: $spacing-s;
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
  }

  .header {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .lock {
    font-size: 0.75rem;
    line-height: 1;
  }

  .author {
    font-family: 'Inter', sans-serif;
    font-size: 0.72rem;
    font-weight: 600;
    color: $brand-muted;
  }

  .date {
    font-family: 'Inter', sans-serif;
    font-size: 0.72rem;
    color: $text-secondary;
    opacity: 0.7;
    margin-left: auto;
  }

  .content {
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    line-height: 1.55;
    color: $text-primary;
    margin: 0;
    white-space: pre-wrap;
  }
}

// ── Document ───────────────────────────────────────────────────────────────────
.doc-item {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: 10px $spacing-s;
  background: #fff;
  border-radius: $radius-md;
  border: 0.5px solid rgba(47, 42, 37, 0.1);
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.07);

  .icon {
    flex-shrink: 0;
    width: 36px;
    height: 36px;
    border-radius: $radius-sm;
    display: flex;
    align-items: center;
    justify-content: center;
    font-family: 'Inter', sans-serif;
    font-size: 9px;
    font-weight: 700;
    letter-spacing: 0.04em;

    &.pdf {
      background: rgba(163, 45, 45, 0.1);
      color: #a32d2d;
    }
    &.image {
      background: rgba(24, 95, 165, 0.1);
      color: #185fa5;
    }
    &.other {
      background: rgba(107, 99, 92, 0.1);
      color: #6b635c;
    }
  }

  .info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  .name {
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    font-weight: 500;
    color: $text-primary;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .meta {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    color: $text-secondary;
    opacity: 0.7;
  }

  .actions {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    flex-shrink: 0;
  }

  .btn {
    width: 28px;
    height: 28px;
    border-radius: $radius-sm;
    border: none;
    background: none;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.875rem;
    cursor: pointer;
    text-decoration: none;
    color: $text-secondary;
    transition:
      background 150ms ease,
      color 150ms ease;

    &:hover {
      background: $surface-soft;
      color: $text-primary;
    }
    &.delete:hover {
      background: rgba(163, 45, 45, 0.1);
      color: #a32d2d;
    }
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
