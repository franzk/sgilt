<!--
  Bouton « + Document » d'une réservation : ouvre le sélecteur de fichier, refuse un fichier de plus
  de 10 Mo (événement `error` avec le message, que l'appelant affiche) et transmet le fichier valide
  (événement `upload`). Pendant un envoi, il affiche un indicateur de chargement et bloque un nouveau
  choix.
  C'est un <label> qui contient le champ fichier caché : un clic dessus ouvre directement le
  sélecteur. Son apparence vient de l'appelant, par une classe posée sur cette racine.
  Utilisé dans la frise (ReservationFeed, à côté de « + Note ») et dans l'onglet Documents
  (ReservationDocuments).
-->
<template>
  <label class="document-upload" :class="{ uploading }">
    <input
      type="file"
      class="input"
      :disabled="uploading"
      @change="onChange"
    />
    <span v-if="uploading" class="spinner" aria-hidden="true" />
    <AttachmentIcon v-else class="icon" aria-hidden="true" />
    {{ uploading ? $t('feed.actions.uploading') : $t('feed.actions.add-document') }}
  </label>
</template>

<script setup lang="ts">
import { AttachmentIcon } from '@remixicons/vue/line'

defineProps<{
  // Envoi en cours : chargement, pas de nouveau choix.
  uploading: boolean
}>()

const emit = defineEmits<{
  upload: [file: File]
  // Message d'erreur du dernier choix, null s'il est valide.
  error: [message: string | null]
}>()

const { t } = useI18n()

const MAX_FILE_SIZE = 10 * 1024 * 1024 // 10 Mo

function onChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  input.value = ''
  if (!file || !file.size) return
  if (file.size > MAX_FILE_SIZE) {
    emit('error', t('feed.upload-error.too-large'))
    return
  }
  emit('error', null)
  emit('upload', file)
}
</script>

<style scoped lang="scss">
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.document-upload {
  cursor: pointer;

  &.uploading {
    cursor: default;
  }

  .input {
    display: none;
  }

  .icon {
    width: 1.125rem;
    height: 1.125rem;
  }

  .spinner {
    display: inline-block;
    width: 0.75rem;
    height: 0.75rem;
    border: 2px solid currentColor;
    border-top-color: transparent;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }
}
</style>
