<template>
  <SgiltDialog
    v-model:open="open"
    :title="$t('prestataire.media-dialog.title')"
    fullscreen
    max-width="640px"
  >
    <div class="heroboard">
      <!-- Grille 5 slots -->
      <div class="slot-grid">
        <button
          v-for="({ slot, thumbnailUrl }, i) in slotDisplays"
          :key="i"
          class="slot-tile"
          :class="{ selected: i === activeSlotIndex, empty: !slot }"
          type="button"
          @click="selectSlot(i)"
        >
          <img v-if="thumbnailUrl" :src="thumbnailUrl" alt="" class="tile-img" />
          <span v-else class="tile-empty" aria-hidden="true">+</span>
          <span v-if="slot?.type === 'YOUTUBE'" class="tile-play" aria-hidden="true">▶</span>
        </button>
      </div>

      <!-- Preview du slot actif -->
      <div class="preview">
        <img v-if="activePreviewUrl" :src="activePreviewUrl" alt="Aperçu" class="preview-img" />
        <p v-else class="preview-empty">{{ $t('prestataire.media-dialog.preview-empty') }}</p>
      </div>

      <!-- Contrôles du slot actif -->
      <div class="slot-controls">
        <button class="upload-btn" type="button" @click="uploadInputRef?.click()">
          <ImageAddIcon />
          {{ $t('prestataire.media-dialog.upload-button') }}
        </button>
        <input
          ref="uploadInputRef"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleUpload"
        />

        <!-- Champ YouTube — slots 1 à 4 seulement (réservé à slot 0 pour la hauteur) -->
        <div
          class="youtube-field"
          :style="activeSlotIndex === 0 ? { visibility: 'hidden', pointerEvents: 'none' } : {}"
          :aria-hidden="activeSlotIndex === 0 ? 'true' : undefined"
        >
          <div class="youtube-row">
            <input
              v-model="youtubeInput"
              class="youtube-input"
              type="text"
              :placeholder="$t('prestataire.media-dialog.youtube-placeholder')"
              @input="youtubeError = null"
              @keydown.enter="handleYoutubeUrl"
            />
            <button class="youtube-ok" type="button" @click="handleYoutubeUrl">
              {{ $t('prestataire.media-dialog.youtube-ok') }}
            </button>
          </div>
          <p v-if="youtubeError" class="field-error">{{ youtubeError }}</p>
        </div>

        <button v-if="activeSlot" class="delete-btn" type="button" @click="deleteActiveSlot">
          {{ $t('prestataire.media-dialog.delete-media') }}
        </button>
      </div>

      <div class="actions">
        <p v-if="saveError" class="save-error">{{ saveError }}</p>
        <SgiltButton variant="secondary" @click="open = false">Annuler</SgiltButton>
        <SgiltButton @click="handleSave">Enregistrer</SgiltButton>
      </div>
    </div>
  </SgiltDialog>
</template>

<script setup lang="ts">
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { ImageAddIcon } from '@remixicons/vue/line'
import type { PrestataireDetail } from '~/data/prestataire/domain/PrestataireDetail'

const props = defineProps<{ prestataire: PrestataireDetail }>()
const open = defineModel<boolean>('open', { required: true })

const { t } = useI18n()
const { toUrl } = useImageUrl()

// ── Types locaux ──────────────────────────────────────────────────────────────

type DraftImageSlot = { type: 'IMAGE'; ref: string }
type DraftPendingSlot = { type: 'PENDING'; pendingFile: File; previewUrl: string }
type DraftYoutubeSlot = { type: 'YOUTUBE'; ref: string }
type DraftSlot = null | DraftImageSlot | DraftPendingSlot | DraftYoutubeSlot

const SLOT_COUNT = 5

// ── State ─────────────────────────────────────────────────────────────────────

const slots = ref<DraftSlot[]>(Array.from({ length: SLOT_COUNT }, () => null))
const activeSlotIndex = ref(0)
const youtubeInput = ref('')
const youtubeError = ref<string | null>(null)
const saveError = ref<string | null>(null)
const uploadInputRef = ref<HTMLInputElement | null>(null)

// ── Helpers ───────────────────────────────────────────────────────────────────

function revokeSlotBlob(slot: DraftSlot): void {
  if (slot?.type === 'PENDING') URL.revokeObjectURL(slot.previewUrl)
}

function slotThumbnailUrl(slot: DraftSlot): string | null {
  if (!slot) return null
  if (slot.type === 'PENDING') return slot.previewUrl
  if (slot.type === 'YOUTUBE') return youtubeThumbnailUrl(slot.ref)
  return toUrl(slot.ref)
}

// ── Computed ──────────────────────────────────────────────────────────────────

const activeSlot = computed<DraftSlot>(() => slots.value[activeSlotIndex.value] ?? null)

const activePreviewUrl = computed<string | null>(() => slotThumbnailUrl(activeSlot.value))

const slotDisplays = computed(() =>
  slots.value.map((slot) => ({ slot, thumbnailUrl: slotThumbnailUrl(slot) })),
)

// ── Initialisation ────────────────────────────────────────────────────────────

function resetDraft(): void {
  slots.value.forEach(revokeSlotBlob)
  const fresh: DraftSlot[] = Array.from({ length: SLOT_COUNT }, () => null)
  for (const media of props.prestataire.medias) {
    if (media.position >= 0 && media.position < SLOT_COUNT) {
      fresh[media.position] =
        media.type === 'IMAGE'
          ? { type: 'IMAGE', ref: media.ref }
          : { type: 'YOUTUBE', ref: media.ref }
    }
  }
  slots.value = fresh
  activeSlotIndex.value = 0
  youtubeInput.value = ''
  youtubeError.value = null
  saveError.value = null
}

watch(open, (val) => {
  if (val) resetDraft()
})

watch(
  () => slots.value[0],
  () => {
    saveError.value = null
  },
)

onUnmounted(() => {
  slots.value.forEach(revokeSlotBlob)
})

// ── Sélection de slot ─────────────────────────────────────────────────────────

function selectSlot(index: number): void {
  activeSlotIndex.value = index
  const slot = slots.value[index]
  youtubeInput.value = slot?.type === 'YOUTUBE' ? `https://www.youtube.com/watch?v=${slot.ref}` : ''
  youtubeError.value = null
}

// ── Upload photo ──────────────────────────────────────────────────────────────

function handleUpload(e: Event): void {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  const idx = activeSlotIndex.value
  revokeSlotBlob(slots.value[idx] ?? null)
  const previewUrl = URL.createObjectURL(file)
  slots.value[idx] = { type: 'PENDING', pendingFile: file, previewUrl }
  ;(e.target as HTMLInputElement).value = ''
}

// ── URL YouTube ───────────────────────────────────────────────────────────────

function handleYoutubeUrl(): void {
  const id = extractYoutubeId(youtubeInput.value)
  if (!id) {
    youtubeError.value = t('prestataire.media-dialog.youtube-error')
    return
  }
  youtubeError.value = null
  const idx = activeSlotIndex.value
  revokeSlotBlob(slots.value[idx] ?? null)
  slots.value[idx] = { type: 'YOUTUBE', ref: id }
  youtubeInput.value = ''
}

// ── Suppression ───────────────────────────────────────────────────────────────

function deleteActiveSlot(): void {
  const idx = activeSlotIndex.value
  revokeSlotBlob(slots.value[idx] ?? null)
  slots.value[idx] = null
}

// ── Enregistrer (stub) ────────────────────────────────────────────────────────

function handleSave(): void {
  if (!slots.value[0]) {
    saveError.value = t('prestataire.media-dialog.error-no-hero')
    return
  }
  saveError.value = null
  const compacted = slots.value
    .filter((slot): slot is NonNullable<DraftSlot> => slot !== null)
    .map((slot, position) => ({ ...slot, position }))
  console.log('[HeroboardDialog] medias stub:', compacted)
  open.value = false
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.heroboard {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  padding: $spacing-m $spacing-l $spacing-l;

  // ── Grille 5 slots ───────────────────────────────────────────────────────────
  .slot-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: $spacing-xs;

    .slot-tile {
      position: relative;
      aspect-ratio: 3/2;
      border-radius: $radius-sm;
      border: 2.5px solid transparent;
      background: $surface-soft;
      cursor: pointer;
      overflow: hidden;
      padding: 0;
      transition: border-color 120ms ease;

      &.selected {
        border-color: $brand-accent;
      }

      &.empty {
        border-style: dashed;
        border-color: $divider-color;

        &.selected {
          border-color: $brand-accent;
          border-style: solid;
        }
      }

      .tile-img {
        position: absolute;
        inset: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .tile-empty {
        position: absolute;
        inset: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.2rem;
        color: $text-secondary;
      }

      .tile-play {
        position: absolute;
        inset: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        background: rgba(0, 0, 0, 0.3);
        color: #fff;
        font-size: 0.65rem;
      }
    }
  }

  // ── Preview ───────────────────────────────────────────────────────────────────
  .preview {
    flex: 1;
    min-height: 0;
    width: 100%;
    border-radius: $radius-sm;
    border: 2.5px solid $brand-accent;
    background: $surface-soft;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;

    .preview-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .preview-empty {
      margin: 0;
      font-size: 0.8rem;
      color: $text-secondary;
      text-align: center;
      padding: $spacing-m;
    }
  }

  // ── Contrôles du slot ─────────────────────────────────────────────────────────
  .slot-controls {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;

    .upload-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
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

      svg {
        width: 16px;
        height: 16px;
        flex-shrink: 0;
      }
    }

    .youtube-field {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .youtube-row {
        display: flex;
        gap: $spacing-xs;
      }

      .youtube-input {
        flex: 1;
        padding: $spacing-xs $spacing-s;
        border: 1px solid $divider-color;
        border-radius: $radius-sm;
        font-family: inherit;
        font-size: 0.85rem;
        color: $text-primary;
        background: $surface-soft;
        outline: none;
        transition:
          border-color 150ms ease,
          box-shadow 150ms ease;

        &:focus {
          border-color: $input-focus-border-color;
          box-shadow: $input-focus-box-shadow;
          background: #fff;
        }
      }

      .youtube-ok {
        padding: $spacing-xs $spacing-s;
        border: 1px solid $divider-color;
        border-radius: $radius-sm;
        background: $surface-soft;
        font-family: inherit;
        font-size: 0.85rem;
        cursor: pointer;
        white-space: nowrap;
        transition: background 150ms ease;

        &:hover {
          background: $divider-color;
        }
      }

      .field-error {
        margin: 0;
        font-size: 0.75rem;
        color: $color-error;
      }
    }

    .delete-btn {
      align-self: flex-start;
      padding: 0;
      border: none;
      background: none;
      font-family: inherit;
      font-size: 0.8rem;
      color: $color-error;
      cursor: pointer;
      text-decoration: underline;
      transition: opacity 150ms ease;

      &:hover {
        opacity: 0.7;
      }
    }
  }

  // ── Actions globales (erreur inline + boutons) ───────────────────────────────
  .actions {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: $spacing-s;

    .save-error {
      flex: 1;
      margin: 0;
      font-size: 0.82rem;
      color: $color-error;
    }
  }
}
</style>
