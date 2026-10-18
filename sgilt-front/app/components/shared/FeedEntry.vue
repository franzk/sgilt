<template>
  <!-- ══ Note (de l'organisateur, du prestataire, ou d'un changement de statut) ══ -->
  <FeedEntryCard
    v-if="item.type === 'note' && !item.isMessageInitial"
    :kind="kind"
    :icon="icon"
    :pastille-style="pastilleStyle"
    :title="noteTitle"
    :created-at="item.createdAt"
    :author="item.author"
    :is-own="isOwn"
  >
    <p v-if="item.content" class="content">{{ item.content }}</p>
    <template #foot>
      <span class="visibility" :class="{ private: item.isPersonal }">
        <LockIcon v-if="item.isPersonal" class="icon" aria-hidden="true" />
        {{ item.isPersonal ? $t('feed.visibility.private') : $t('feed.visibility.public') }}
      </span>
    </template>
  </FeedEntryCard>

  <!-- ══ Demande initiale (toujours partagée : pas de badge de visibilité) ══ -->
  <FeedEntryCard
    v-else-if="item.type === 'note'"
    kind="initial"
    :icon="icon"
    :title="$t('feed.note-initial-label')"
    :created-at="item.createdAt"
    :author="item.author"
    :is-own="isOwn"
  >
    <strong class="subtitle">{{ item.title }}</strong>
    <p v-if="item.content" class="content">{{ item.content }}</p>
  </FeedEntryCard>

  <!-- ══ Document : le fichier, avec ses actions dans le menu ⋮ ══ -->
  <FeedEntryCard
    v-else
    kind="document"
    :icon="icon"
    :title="$t('feed.document.title')"
    :created-at="item.createdAt"
    :author="item.author"
    :is-own="isOwn"
  >
    <DocumentFile
      :document="item"
      :can-delete="isOwn"
      @download="(url, fileName) => $emit('download', url, fileName)"
      @delete="(id) => $emit('delete', id)"
    />
  </FeedEntryCard>
</template>

<script setup lang="ts">
// Carte d'un élément du fil d'une réservation (frise « Notes & suivi »), commune au client et au
// prestataire. Trois blocs distincts dans le template, chacun dans la coquille FeedEntryCard
// (pastille, titre, date, auteur) : une note (de l'organisateur, du prestataire ou d'un changement de
// statut), la demande initiale, un document (fichier et menu : télécharger, supprimer le sien).
import {
  AttachmentIcon,
  ArrowRightCircleIcon,
  CheckboxCircleIcon,
  CloseCircleIcon,
  FileTextIcon,
  FileUploadIcon,
  LockIcon,
} from '@remixicons/vue/line'
import type { Component } from 'vue'
import DocumentFile from '~/components/shared/DocumentFile.vue'
import FeedEntryCard from '~/components/shared/FeedEntryCard.vue'
import { RESERVATION_STATUS_CONFIG } from '~/constants/reservation-status'
import type { FeedItem } from '~/data/reservation/domain/FeedItem'
import type { ReservationStatus } from '~/data/reservation/domain/ReservationStatus'

const props = defineProps<{
  item: FeedItem
  currentUserRole: 'client' | 'prestataire'
}>()

defineEmits<{
  download: [url: string, fileName: string]
  delete: [id: string]
}>()

const { t } = useI18n()

// ── Type de carte ─────────────────────────────────────────────────────────────
type EntryKind = 'initial' | 'status' | 'note-client' | 'note-prestataire' | 'document'

const kind = computed<EntryKind>(() => {
  if (props.item.type === 'document') return 'document'
  if (props.item.isMessageInitial) return 'initial'
  if (props.item.generatedKey) return 'status'
  return props.item.author.role === 'prestataire' ? 'note-prestataire' : 'note-client'
})

// Changement de statut : la note système et le statut qu'elle annonce (couleur, icône).
const STATUS_OF_SYSTEM_NOTE: Record<string, ReservationStatus> = {
  'feed.system.contacted': 'en_discussion',
  'feed.system.confirmed': 'confirmee',
  'feed.system.cancelled': 'annulee',
  'feed.system.refused': 'refusee',
}
const STATUS_ICONS: Partial<Record<ReservationStatus, Component>> = {
  en_discussion: ArrowRightCircleIcon,
  confirmee: CheckboxCircleIcon,
  annulee: CloseCircleIcon,
  refusee: CloseCircleIcon,
}
const status = computed(() =>
  props.item.generatedKey ? STATUS_OF_SYSTEM_NOTE[props.item.generatedKey] : undefined,
)

const ICONS: Record<Exclude<EntryKind, 'status'>, Component> = {
  initial: FileUploadIcon,
  'note-client': FileTextIcon,
  'note-prestataire': FileTextIcon,
  document: AttachmentIcon,
}
const icon = computed(() =>
  kind.value === 'status'
    ? ((status.value && STATUS_ICONS[status.value]) ?? CheckboxCircleIcon)
    : ICONS[kind.value],
)

// Seule la couleur d'un changement de statut vient d'ailleurs (celle du statut) ; les autres sont
// dans les styles, par type.
const pastilleStyle = computed(() =>
  kind.value === 'status' && status.value
    ? { background: RESERVATION_STATUS_CONFIG[status.value].color }
    : undefined,
)

// ── Contenu ───────────────────────────────────────────────────────────────────
// Note : le titre saisi par l'auteur, ou celui du changement de statut.
const noteTitle = computed(() =>
  props.item.generatedKey
    ? t(props.item.generatedKey, { name: props.item.author.name })
    : props.item.title,
)

const isOwn = computed(() => props.item.author.role === props.currentUserRole)


</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Contenu des cartes (dans la coquille FeedEntryCard).
.subtitle {
  font-size: $font-size-sm;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.content {
  margin: 0;
  font-size: $font-size-sm;
  line-height: 1.55;
  color: $text-secondary;
  white-space: pre-wrap;
}

// ── Note : visibilité, dans le pied ────────────────────────────────────────────
.visibility {
  display: inline-flex;
  align-items: center;
  gap: $spacing-xxs;
  padding: 0.125rem $spacing-s;
  border-radius: 9999px;
  background: $brand-subtle;
  color: $text-secondary;
  font-size: $font-size-xs;

  &.private {
    background: rgba(217, 48, 37, 0.08);
    color: #d93025;
  }

  .icon {
    width: 0.85rem;
    height: 0.85rem;
  }
}
</style>
