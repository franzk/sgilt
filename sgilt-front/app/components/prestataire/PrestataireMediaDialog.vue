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
          v-for="(s, i) in slotDisplays"
          :key="i"
          class="slot-tile"
          :class="{
            selected: i === activeSlotIndex,
            empty: s.status === 'EMPTY',
            uploading: s.isUploading,
          }"
          type="button"
          @click="selectSlot(i)"
        >
          <img
            v-if="s.thumbnailUrl"
            :src="s.thumbnailUrl"
            alt=""
            class="tile-img"
            :class="{ dim: s.isUploading }"
          />
          <span v-if="s.isUploading" class="tile-spinner" aria-hidden="true" />
          <span v-else-if="s.errorMessage" class="tile-error-icon" aria-hidden="true">!</span>
          <span v-else-if="s.status === 'EMPTY'" class="tile-empty" aria-hidden="true">+</span>
          <span
            v-if="s.type === 'YOUTUBE' && s.status === 'OCCUPIED'"
            class="tile-play"
            aria-hidden="true"
            >▶</span
          >
        </button>
      </div>

      <!-- Preview du slot actif -->
      <div class="preview">
        <img
          v-if="activeSlot.thumbnailUrl && !activeSlot.isUploading"
          :src="activeSlot.thumbnailUrl"
          alt="Aperçu"
          class="preview-img"
        />
        <div v-else-if="activeSlot.isUploading" class="preview-loading" aria-hidden="true">
          <span class="preview-spinner" />
        </div>
        <p v-else-if="activeSlot.errorMessage" class="preview-error">
          {{ activeSlot.errorMessage }}
        </p>
        <p v-else class="preview-empty">{{ $t('prestataire.media-dialog.preview-empty') }}</p>
      </div>

      <!-- Contrôles du slot actif -->
      <div class="slot-controls">
        <!-- Bouton upload -->
        <button
          class="upload-btn"
          type="button"
          :disabled="activeSlot.isUploading"
          @click="uploadInputRef?.click()"
        >
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

        <!-- Bouton supprimer -->
        <button
          v-if="activeSlot.status === 'OCCUPIED' || activeSlot.status === 'ERROR'"
          class="delete-btn"
          type="button"
          @click="deleteActiveSlot"
        >
          {{ $t('prestataire.media-dialog.delete-media') }}
        </button>
      </div>

      <div class="actions">
        <p v-if="saveError" class="save-error">{{ saveError }}</p>
        <SgiltButton variant="secondary" :disabled="isSaving" @click="open = false">{{ $t('common.cancel') }}</SgiltButton>
        <SgiltButton :loading="isSaving" @click="handleSave">{{ $t('common.save') }}</SgiltButton>
      </div>
    </div>
  </SgiltDialog>
</template>

<script setup lang="ts">
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { ImageAddIcon } from '@remixicons/vue/line'
import type { PrestataireDetail } from '~/data/prestataire/domain/PrestataireDetail'
import type { Media } from '~/data/prestataire/domain/Media'

const props = defineProps<{ prestataire: PrestataireDetail }>()
const open = defineModel<boolean>('open', { required: true })

const { t } = useI18n()
const { toUrl } = useImageUrl()
const { saveMedias, uploadMedia } = usePrestataire()

// ── Types locaux ──────────────────────────────────────────────────────────────

type SlotStatus = 'OCCUPIED' | 'UPLOADING' | 'ERROR' | 'EMPTY'

type DraftSlot = {
  type: 'IMAGE' | 'YOUTUBE' | null
  ref: string | null
  status: SlotStatus
}

type SlotDisplay = DraftSlot & {
  isUploading: boolean
  errorMessage: string | null
  thumbnailUrl: string | null
}

const SLOT_COUNT = 5

const emptySlot = (): DraftSlot => ({ type: null, ref: null, status: 'EMPTY' })

// ── State ─────────────────────────────────────────────────────────────────────

const slots = ref<DraftSlot[]>(Array.from({ length: SLOT_COUNT }, emptySlot))
const activeSlotIndex = ref(0)
const youtubeInput = ref('')
const youtubeError = ref<string | null>(null)
const saveError = ref<string | null>(null)
const isSaving = ref(false)
const uploadInputRef = ref<HTMLInputElement | null>(null)

// ── Helpers ───────────────────────────────────────────────────────────────────

function toSlotDisplay(slot: DraftSlot): SlotDisplay {
  const thumbnailUrl =
    slot.status === 'OCCUPIED' && slot.ref
      ? slot.type === 'YOUTUBE'
        ? youtubeThumbnailUrl(slot.ref)
        : toUrl(slot.ref)
      : null
  return {
    ...slot,
    isUploading: slot.status === 'UPLOADING',
    errorMessage: slot.status === 'ERROR' ? t('prestataire.media-dialog.upload-error') : null,
    thumbnailUrl,
  }
}

// ── Computed ──────────────────────────────────────────────────────────────────

const slotDisplays = computed<SlotDisplay[]>(() => slots.value.map(toSlotDisplay))

const activeSlot = computed<SlotDisplay>(
  () => slotDisplays.value[activeSlotIndex.value] ?? toSlotDisplay(emptySlot()),
)

// ── Initialisation ────────────────────────────────────────────────────────────

function resetDraft(): void {
  const fresh: DraftSlot[] = Array.from({ length: SLOT_COUNT }, emptySlot)
  for (const media of props.prestataire.medias) {
    if (media.position >= 0 && media.position < SLOT_COUNT) {
      fresh[media.position] = { type: media.type, ref: media.ref, status: 'OCCUPIED' }
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

// ── Sélection de slot ─────────────────────────────────────────────────────────

function selectSlot(index: number): void {
  activeSlotIndex.value = index
  const slot = slots.value[index]
  youtubeInput.value = slot?.type === 'YOUTUBE' ? youtubeWatchUrl(slot.ref) : ''
  youtubeError.value = null
}

// ── Upload photo ──────────────────────────────────────────────────────────────

async function handleUpload(e: Event): Promise<void> {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  const idx = activeSlotIndex.value
  ;(e.target as HTMLInputElement).value = ''
  slots.value[idx] = { type: null, ref: null, status: 'UPLOADING' }
  try {
    const { key } = await uploadMedia(file)
    slots.value[idx] = { type: 'IMAGE', ref: key, status: 'OCCUPIED' }
  } catch (e) {
    console.error('[PrestataireMediaDialog] upload failed', e)
    slots.value[idx] = { type: null, ref: null, status: 'ERROR' }
  }
}

// ── URL YouTube ───────────────────────────────────────────────────────────────

function handleYoutubeUrl(): void {
  const id = extractYoutubeId(youtubeInput.value)
  if (!id) {
    youtubeError.value = t('prestataire.media-dialog.youtube-error')
    return
  }
  youtubeError.value = null
  slots.value[activeSlotIndex.value] = { type: 'YOUTUBE', ref: id, status: 'OCCUPIED' }
  youtubeInput.value = ''
}

// ── Suppression ───────────────────────────────────────────────────────────────

function deleteActiveSlot(): void {
  slots.value[activeSlotIndex.value] = emptySlot()
}

// ── Enregistrer ───────────────────────────────────────────────────────────────

async function handleSave(): Promise<void> {
  if (slots.value[0]?.status !== 'OCCUPIED') {
    saveError.value = t('prestataire.media-dialog.error-no-hero')
    return
  }

  saveError.value = null
  const medias: Media[] = slots.value
    .filter(
      (slot): slot is DraftSlot & { type: 'IMAGE' | 'YOUTUBE'; ref: string } =>
        slot.status === 'OCCUPIED' && slot.type !== null && slot.ref !== null,
    )
    .map((slot, position) => ({ type: slot.type, ref: slot.ref, position }))

  isSaving.value = true
  try {
    await saveMedias(medias)
    open.value = false
  } catch (e) {
    console.error('[PrestataireMediaDialog] save failed', e)
    saveError.value = t('prestataire.media-dialog.save-error')
  } finally {
    isSaving.value = false
  }
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

      &.uploading {
        cursor: default;
      }

      .tile-img {
        position: absolute;
        inset: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;

        &.dim {
          opacity: 0.4;
        }
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

      .tile-spinner {
        position: absolute;
        inset: 0;
        display: flex;
        align-items: center;
        justify-content: center;

        &::after {
          content: '';
          width: 14px;
          height: 14px;
          border: 2px solid rgba(255, 255, 255, 0.4);
          border-top-color: #fff;
          border-radius: 50%;
          animation: tile-spin 0.7s linear infinite;
        }
      }

      .tile-error-icon {
        position: absolute;
        inset: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 0.85rem;
        font-weight: 700;
        color: $color-error;
        background: rgba(255, 255, 255, 0.85);
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

    .preview-loading {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;

      .preview-spinner {
        display: block;
        width: 28px;
        height: 28px;
        border: 3px solid rgba(0, 0, 0, 0.12);
        border-top-color: $brand-accent;
        border-radius: 50%;
        animation: tile-spin 0.7s linear infinite;
      }
    }

    .preview-error {
      margin: 0;
      font-size: 0.8rem;
      color: $color-error;
      text-align: center;
      padding: $spacing-m;
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

  // ── Upload button disabled ────────────────────────────────────────────────────
  .upload-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    pointer-events: none;
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

@keyframes tile-spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
