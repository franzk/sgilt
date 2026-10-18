<!--
  Onglet Documents de la fiche d'une réservation, le même pour le client et le prestataire : tous les
  documents de la réservation, du plus récent au plus ancien (auteur et date sous chaque nom), le
  bouton « + Document », le téléchargement, et la suppression de ses propres documents.
  Il reçoit les éléments du fil (ceux de la frise du Suivi) et n'en garde que les documents : un
  document ajouté ici apparaît aussi dans la frise, et inversement. Il ne fait aucun appel au back :
  ReservationBoard fait l'envoi, le téléchargement et la suppression.
-->
<template>
  <div class="reservation-documents">
    <div class="header">
      <h2 class="title">{{ $t('reservation.documents.title') }}</h2>
      <p class="subtitle">{{ $t('reservation.documents.subtitle') }}</p>
    </div>

    <DocumentUploadButton
      v-if="canUpload"
      class="upload"
      :uploading="uploading"
      @upload="(file) => $emit('upload', file)"
      @error="(message) => (uploadError = message)"
    />
    <p v-if="uploadError" class="upload-error">{{ uploadError }}</p>

    <div v-if="loading" class="list">
      <Sk v-for="n in 3" :key="n" height="3.5rem" radius="0.625rem" />
    </div>

    <!-- Du plus récent au plus ancien. -->
    <ul v-else-if="sortedDocuments.length > 0" class="list">
      <li v-for="document in sortedDocuments" :key="document.id">
        <DocumentFile
          :document="document"
          :can-delete="document.author.role === currentUserRole"
          @download="(url, fileName) => $emit('download', url, fileName)"
          @delete="(id) => $emit('delete', id)"
        >
          <template #meta>
            <span class="meta">
              {{ document.author.role === currentUserRole ? $t('feed.you') : document.author.name }}
              · {{ formatRelativeDateTime(document.createdAt) }}
            </span>
          </template>
        </DocumentFile>
      </li>
    </ul>

    <p v-else class="empty">{{ $t('reservation.documents.empty') }}</p>
  </div>
</template>

<script setup lang="ts">
import Sk from '~/components/basics/Sk.vue'
import DocumentFile from '~/components/shared/DocumentFile.vue'
import DocumentUploadButton from '~/components/shared/DocumentUploadButton.vue'
import type { FeedItem } from '~/data/reservation/domain/FeedItem'
import { formatRelativeDateTime } from '~/utils/dateUtils'

const props = defineProps<{
  // Éléments du fil de la réservation : seuls les documents sont affichés.
  items: FeedItem[]
  currentUserRole: 'client' | 'prestataire'
  loading: boolean
  canUpload: boolean
  uploading: boolean
}>()

defineEmits<{
  upload: [file: File]
  download: [url: string, fileName: string]
  delete: [id: string]
}>()

const sortedDocuments = computed(() =>
  props.items
    .filter((item) => item.type === 'document')
    .sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime()),
)

// Erreur du dernier fichier choisi (trop lourd).
const uploadError = ref<string | null>(null)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.reservation-documents {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;

  .header {
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

  // Racine de DocumentUploadButton : même bouton que « + Document » dans la frise.
  .upload {
    display: inline-flex;
    align-self: flex-start;
    align-items: center;
    justify-content: center;
    gap: $spacing-xs;
    padding: $spacing-s $spacing-l;
    border: 1px solid $divider-color;
    border-radius: 9999px;
    background: $surface-white;
    color: $text-primary;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    transition: border-color 150ms ease;

    &:hover {
      border-color: $brand-primary;
    }
  }

  .upload-error {
    margin: 0;
    font-size: $font-size-sm;
    color: $state-error;
  }

  .list {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
    margin: 0;
    padding: 0;
    list-style: none;
  }

  .meta {
    font-size: $font-size-xs;
    color: $text-secondary;
  }

  .empty {
    margin: 0;
    padding: $spacing-m 0;
    font-size: $font-size-sm;
    font-style: italic;
    color: $text-secondary;
    text-align: center;
  }
}
</style>
