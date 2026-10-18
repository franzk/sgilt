<!--
  Un document d'une réservation : son type (PDF, IMG, DOC), son nom et un menu ⋮ (télécharger ;
  supprimer s'il appartient à l'utilisateur courant). Un emplacement `meta` permet d'ajouter un
  contexte sous le nom (ex. auteur et date).
  Utilisé dans la frise (carte « Document ajouté » de FeedEntry) et dans l'onglet Documents
  (ReservationDocuments), côté client comme prestataire.
-->
<template>
  <div class="document-file">
    <span class="file-icon" :class="fileKind">{{ $t(`feed.document.type.${fileKind}`) }}</span>
    <span class="file-text">
      <span class="file-name">{{ document.name }}</span>
      <!-- Contexte du fichier, au choix de l'appelant (ex. auteur et date dans la liste des documents). -->
      <slot name="meta" />
    </span>
    <div ref="menuRef" class="menu">
      <button
        class="menu-btn"
        type="button"
        :aria-label="$t('feed.document.menu-aria')"
        @click="menuOpen = !menuOpen"
      >
        <More2Icon class="icon" />
      </button>
      <ul v-if="menuOpen" class="menu-list">
        <li>
          <button class="menu-item" type="button" @click="onDownload">
            <DownloadIcon class="icon" />{{ $t('feed.document.download') }}
          </button>
        </li>
        <li v-if="canDelete">
          <button class="menu-item danger" type="button" @click="onDelete">
            <DeleteBinIcon class="icon" />{{ $t('feed.document.delete') }}
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onClickOutside } from '@vueuse/core'
import { DeleteBinIcon, DownloadIcon, More2Icon } from '@remixicons/vue/line'
import type { FeedItem } from '~/data/reservation/domain/FeedItem'

const props = defineProps<{
  document: FeedItem
  // Document de l'utilisateur courant : lui seul peut le supprimer.
  canDelete: boolean
}>()

const emit = defineEmits<{
  download: [url: string, fileName: string]
  delete: [id: string]
}>()

const fileKind = computed(() => {
  if (props.document.fileType === 'pdf') return 'pdf'
  if (props.document.fileType === 'image') return 'image'
  return 'other'
})

// ── Menu ──────────────────────────────────────────────────────────────────────
const menuOpen = ref(false)
const menuRef = ref<HTMLElement | null>(null)
onClickOutside(menuRef, () => (menuOpen.value = false))

function onDownload() {
  menuOpen.value = false
  emit('download', props.document.url ?? '', props.document.name ?? '')
}

function onDelete() {
  menuOpen.value = false
  emit('delete', props.document.id)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.document-file {
  display: flex;
  align-items: center;
  gap: $spacing-s;
  padding: $spacing-s;
  border: 1px solid $divider-color;
  border-radius: $radius-md;
  background: $surface-white;

  .file-icon {
    display: flex;
    flex-shrink: 0;
    align-items: center;
    justify-content: center;
    width: 2.25rem;
    height: 2.25rem;
    border-radius: $radius-sm;
    font-size: 0.6rem;
    font-weight: $font-weight-bold;
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

  .file-text {
    display: flex;
    flex: 1;
    flex-direction: column;
    gap: 0.125rem;
    min-width: 0;

    .file-name {
      overflow: hidden;
      font-size: $font-size-sm;
      font-weight: $font-weight-medium;
      color: $text-primary;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .menu {
    position: relative;

    .menu-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 2rem;
      height: 2rem;
      padding: 0;
      border: none;
      border-radius: 50%;
      background: none;
      color: $text-secondary;
      cursor: pointer;

      &:hover {
        background: $surface-soft;
      }

      .icon {
        width: 1.25rem;
        height: 1.25rem;
      }
    }

    .menu-list {
      position: absolute;
      top: 100%;
      right: 0;
      z-index: 5;
      min-width: 10rem;
      margin: $spacing-xxs 0 0;
      padding: $spacing-xxs;
      border: 1px solid $divider-color;
      border-radius: $radius-md;
      background: $surface-white;
      box-shadow: 0 0.25rem 1rem $shadow-m;
      list-style: none;

      .menu-item {
        display: flex;
        align-items: center;
        gap: $spacing-xs;
        width: 100%;
        padding: $spacing-xs $spacing-s;
        border: none;
        border-radius: $radius-sm;
        background: none;
        color: $text-primary;
        font-family: inherit;
        font-size: $font-size-sm;
        text-align: left;
        cursor: pointer;

        &:hover {
          background: $surface-soft;
        }

        &.danger {
          color: #a32d2d;
        }

        .icon {
          width: 1rem;
          height: 1rem;
        }
      }
    }
  }
}
</style>
