<template>
  <SgiltDialog v-model:open="open" title="Modifier l'événement" max-width="560px">
    <div class="form">
      <!-- Titre -->
      <div class="field">
        <label class="label">Titre</label>
        <input v-model="draft.title" class="input" type="text" placeholder="Nom de l'événement" />
      </div>

      <!-- Couverture -->
      <div class="field">
        <label class="label">Image de couverture</label>

        <div class="cover-grid">
          <button
            v-for="[key, url] in coverEntries"
            :key="key"
            class="cover-tile"
            :class="{ selected: draft.coverImage === url }"
            type="button"
            :style="{ backgroundImage: `url(${url})` }"
            @click="draft.coverImage = url"
          >
            <span class="cover-label">{{ COVER_LABELS[key] }}</span>
            <span v-if="draft.coverImage === url" class="cover-check" aria-hidden="true">✓</span>
          </button>
        </div>

        <button class="upload-btn" type="button" @click="uploadRef?.click()">
          <span class="caption"><ImageAddIcon /> Importer ma propre photo</span>
        </button>
        <input
          ref="uploadRef"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleUpload"
        />

        <div
          v-if="!isPresetCover(draft.coverImage)"
          class="custom-preview"
          :style="{ backgroundImage: `url(${draft.coverImage})` }"
        />
      </div>

      <!-- Actions -->
      <div class="actions">
        <SgiltButton variant="secondary" @click="open = false">Annuler</SgiltButton>
        <SgiltButton :disabled="saving" @click="handleSave">
          {{ saving ? 'Enregistrement…' : 'Enregistrer' }}
        </SgiltButton>
      </div>
    </div>
  </SgiltDialog>
</template>

<script setup lang="ts">
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import type { EventDetail, EventPatch } from '~/types/event'
import { DEFAULT_COVERS, resolveEventCover } from '~/utils/eventCovers'
import { ImageAddIcon } from '@remixicons/vue/line'

// ── Constants ─────────────────────────────────────────────────────────────────

const COVER_LABELS: Record<string, string> = {
  mariage: 'Mariage',
  anniversaire: 'Anniversaire',
  soiree_privee: 'Soirée privée',
  fete_entreprise: "Fête d'entreprise",
  autre: 'Autre',
}

const coverEntries = Object.entries(DEFAULT_COVERS) as [string, string][]
const presetUrls = new Set(Object.values(DEFAULT_COVERS))

function isPresetCover(url: string): boolean {
  return presetUrls.has(url)
}

// ── Props / emits ─────────────────────────────────────────────────────────────

interface EditDraft {
  title: string
  coverImage: string
}

const props = defineProps<{ event: EventDetail }>()
const emit = defineEmits<{ save: [patch: EventPatch] }>()

const open = defineModel<boolean>('open', { required: true })

// ── Draft ─────────────────────────────────────────────────────────────────────

const draft = reactive<EditDraft>({ title: '', coverImage: DEFAULT_COVERS.autre! })

watch(open, (val) => {
  if (val) {
    draft.title = props.event.title
    draft.coverImage = resolveEventCover(props.event)
  }
})

// ── Upload ────────────────────────────────────────────────────────────────────

const uploadRef = ref<HTMLInputElement | null>(null)

function handleUpload(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  draft.coverImage = URL.createObjectURL(file)
}

// ── Save ──────────────────────────────────────────────────────────────────────

const saving = ref(false)

async function handleSave() {
  saving.value = true
  const patch: EventPatch = {
    title: draft.title,
    coverImage: draft.coverImage,
  }
  emit('save', patch)
  open.value = false
  saving.value = false
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.form {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-m $spacing-l $spacing-l;

  .field {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;

    .label {
      font-family: 'Inter', sans-serif;
      font-size: 0.75rem;
      font-weight: 600;
      letter-spacing: 0.04em;
      text-transform: uppercase;
      color: $text-secondary;
    }

    .input {
      width: 100%;
      padding: $spacing-xs $spacing-s;
      border: 1px solid $divider-color;
      border-radius: $radius-sm;
      font-family: 'Cormorant Garamond', serif;
      font-size: 1.2rem;
      font-weight: 600;
      color: $brand-primary;
      background: $surface-soft;
      outline: none;
      box-sizing: border-box;
      transition:
        border-color 150ms ease,
        box-shadow 150ms ease;

      &:focus {
        border-color: $input-focus-border-color;
        box-shadow: $input-focus-box-shadow;
        background: #fff;
      }
    }

    .cover-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: $spacing-xs;

      @media (min-width: $desktop) {
        grid-template-columns: repeat(5, 1fr);
      }

      .cover-tile {
        position: relative;
        aspect-ratio: 3/2;
        border-radius: $radius-sm;
        background-size: cover;
        background-position: center;
        border: 2.5px solid transparent;
        cursor: pointer;
        overflow: hidden;
        transition: border-color 120ms ease;
        padding: 0;

        &.selected {
          border-color: $brand-accent;
        }

        .cover-label {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          padding: 4px 6px;
          background: linear-gradient(to top, rgba(0, 0, 0, 0.55), transparent);
          font-family: 'Inter', sans-serif;
          font-size: 0.6rem;
          font-weight: 600;
          color: #fff;
          text-align: center;
        }

        .cover-check {
          position: absolute;
          top: 4px;
          right: 4px;
          width: 18px;
          height: 18px;
          border-radius: 50%;
          background: $brand-accent;
          color: $brand-primary;
          font-size: 0.65rem;
          font-weight: 700;
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }
    }

    .upload-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      padding: $spacing-s;
      border: 1.5px dashed $divider-color;
      border-radius: $radius-sm;
      background: none;
      font-family: inherit;
      font-size: 0.85rem;
      color: $text-secondary;
      cursor: pointer;
      transition:
        border-color 150ms ease,
        color 150ms ease;

      &:hover {
        border-color: $brand-primary;
        color: $brand-primary;
      }

      .caption {
        display: flex;
        align-items: center;
        gap: 6px;

        svg {
          width: 16px;
          height: 16px;
        }
      }
    }

    .custom-preview {
      width: 100%;
      aspect-ratio: 16/9;
      border-radius: $radius-sm;
      background-size: cover;
      background-position: center;
      border: 2.5px solid $brand-accent;
    }
  }

  .actions {
    display: flex;
    gap: $spacing-s;
    justify-content: flex-end;
  }
}
</style>
