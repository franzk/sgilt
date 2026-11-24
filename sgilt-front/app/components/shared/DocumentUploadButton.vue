<!--
  Bouton « + Document » d'une réservation : ouvre le sélecteur de fichiers (plusieurs à la fois),
  refuse tout le choix si un fichier est vide ou dépasse 10 Mo (événement `error` avec le message, que
  l'appelant affiche) et transmet sinon les fichiers choisis (événement `upload`), qui forment un seul
  envoi. Pendant un envoi, il affiche un indicateur de chargement et bloque un nouveau choix.
  C'est un <label> qui contient le champ fichier caché : un clic dessus ouvre directement le
  sélecteur. Son apparence vient de l'appelant, par une classe posée sur cette racine.
  Utilisé dans la frise (ReservationFeed, à côté de « + Note ») et dans l'onglet Documents
  (ReservationDocuments).
-->
<template>
  <label class="document-upload" :class="{ uploading }">
    <input
      type="file"
      multiple
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
  upload: [files: File[]]
  // Message d'erreur du dernier choix, null s'il est valide.
  error: [message: string | null]
}>()

const { t } = useI18n()

const MAX_FILE_SIZE = 10 * 1024 * 1024 // 10 Mo

function onChange(e: Event) {
  const input = e.target as HTMLInputElement
  const files = Array.from(input.files ?? [])
  input.value = ''
  if (files.length === 0) return
  const empty = files.find((file) => file.size === 0)
  if (empty) {
    emit('error', t('feed.upload-error.empty', { name: empty.name }))
    return
  }
  const tooLarge = files.find((file) => file.size > MAX_FILE_SIZE)
  if (tooLarge) {
    emit('error', t('feed.upload-error.too-large', { name: tooLarge.name }))
    return
  }
  emit('error', null)
  emit('upload', files)
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
