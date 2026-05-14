<template>
  <SgiltDialog v-model:open="open" :title="$t('event.edit-dialog.title')" max-width="560px">
    <div class="form">
      <!-- Titre -->
      <div class="field">
        <label class="label">{{ $t('event.edit-dialog.field-title') }}</label>
        <input v-model="draft.title" class="input" type="text" :placeholder="$t('event.edit-dialog.title-placeholder')" />
      </div>

      <!-- Couverture -->
      <div class="field">
        <label class="label">{{ $t('event.edit-dialog.field-cover') }}</label>

        <div class="cover-grid" :class="{ loading: uploading }">
          <button
            v-for="{ key, imageId, url } in bankCovers"
            :key="key"
            class="cover-tile"
            :class="{ selected: draft.activeBankKey === key }"
            type="button"
            :disabled="uploading"
            :style="{ backgroundImage: `url(${url})` }"
            @click="handleBankSelect(key, imageId)"
          >
            <span class="cover-label">{{ $t(`event.edit-dialog.cover-labels.${key}`) }}</span>
            <span v-if="draft.activeBankKey === key" class="cover-check" aria-hidden="true">✓</span>
          </button>
        </div>

        <button class="upload-btn" type="button" :disabled="uploading" @click="uploadRef?.click()">
          <span class="caption">
            <ImageAddIcon />
            {{ uploading ? $t('common.saving') : $t('event.edit-dialog.upload-button') }}
          </span>
        </button>
        <input
          ref="uploadRef"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleUpload"
        />

        <!-- Aperçu d'un upload personnalisé -->
        <div
          v-if="isCustomUpload"
          class="custom-preview"
          :style="{ backgroundImage: `url(${draft.coverDisplayUrl})` }"
        />
      </div>

      <!-- Actions -->
      <div class="actions">
        <SgiltButton variant="secondary" @click="open = false">{{ $t('common.cancel') }}</SgiltButton>
        <SgiltButton :disabled="saving" @click="handleSave">
          {{ saving ? $t('common.saving') : $t('common.save') }}
        </SgiltButton>
      </div>
    </div>
  </SgiltDialog>
</template>

<script setup lang="ts">
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import type { EventDetail } from '~/data/evenement/domain/EventDetail'
import type { EventPatch } from '~/data/evenement/domain/EventPatch'
import { BANK_IMAGE_IDS, resolveEventCover } from '~/utils/eventCovers'
import { uploadEventCover, selectEventCover } from '~/data/evenement/service/evenementService'
import { ImageAddIcon } from '@remixicons/vue/line'

// ── Props / emits ─────────────────────────────────────────────────────────────

const props = defineProps<{
  event: EventDetail
  eventId: string
}>()

const emit = defineEmits<{
  save: [patch: EventPatch]
  coverUpdated: [coverUrl: string]
}>()

const open = defineModel<boolean>('open', { required: true })

// ── Image URL ─────────────────────────────────────────────────────────────────

const { toUrl } = useImageUrl()

const bankCovers = computed(() =>
  Object.entries(BANK_IMAGE_IDS).map(([key, imageId]) => ({ key, imageId, url: toUrl(imageId) })),
)

// ── Draft ─────────────────────────────────────────────────────────────────────

interface EditDraft {
  title: string
  coverDisplayUrl: string
  activeBankKey: string   // clé de la tuile banque sélectionnée, '' = upload perso
}

const draft = reactive<EditDraft>({ title: '', coverDisplayUrl: '', activeBankKey: '' })

watch(open, (val) => {
  if (!val) return
  draft.title = props.event.title
  draft.coverDisplayUrl = resolveEventCover(props.event, toUrl)
  // Détecter si la cover actuelle est une image de banque
  const bankEntry = Object.entries(BANK_IMAGE_IDS).find(
    ([, imageId]) => props.event.coverImage === toUrl(imageId),
  )
  draft.activeBankKey = bankEntry?.[0] ?? ''
})

// Aperçu personnalisé visible uniquement hors sélection banque
const isCustomUpload = computed(
  () => draft.activeBankKey === '' && draft.coverDisplayUrl !== '',
)

// ── Cover — sélection banque ───────────────────────────────────────────────────

const uploading = ref(false)

async function handleBankSelect(key: string, imageId: string): Promise<void> {
  uploading.value = true
  try {
    const coverUrl = await selectEventCover(props.eventId, imageId)
    draft.coverDisplayUrl = coverUrl
    draft.activeBankKey = key
    emit('coverUpdated', coverUrl)
  } finally {
    uploading.value = false
  }
}

// ── Cover — upload ─────────────────────────────────────────────────────────────

const uploadRef = ref<HTMLInputElement | null>(null)

async function handleUpload(e: Event): Promise<void> {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  uploading.value = true
  try {
    const coverUrl = await uploadEventCover(props.eventId, file)
    draft.coverDisplayUrl = coverUrl
    draft.activeBankKey = ''
    emit('coverUpdated', coverUrl)
  } catch (err) {
    console.error('[EventEditDialog] upload cover failed', err)
  } finally {
    uploading.value = false
    // Reset l'input pour permettre de ré-uploader le même fichier
    if (uploadRef.value) uploadRef.value.value = ''
  }
}

// ── Save (titre uniquement) ───────────────────────────────────────────────────

const saving = ref(false)

async function handleSave(): Promise<void> {
  saving.value = true
  emit('save', { title: draft.title })
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
      transition: opacity 150ms ease;

      &.loading { opacity: 0.5; pointer-events: none; }

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

        &.selected { border-color: $brand-accent; }
        &:disabled { cursor: default; }

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

      &:hover:not(:disabled) {
        border-color: $brand-primary;
        color: $brand-primary;
      }

      &:disabled { opacity: 0.5; cursor: default; }

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
