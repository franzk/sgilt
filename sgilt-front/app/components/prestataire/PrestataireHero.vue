<script setup lang="ts">
import SgiltHero from '~/components/basics/media/SgiltHero.vue'
import EditableText from '~/components/prestataire/EditableText.vue'
import PrestataireMediaDialog from '~/components/prestataire/PrestataireMediaDialog.vue'
import { ImageAddIcon } from '@remixicons/vue/line'
import type { PrestataireDetail } from '~/data/prestataire/domain/PrestataireDetail'
import type { DisplayMode } from '~/types/prestataire'

const { t } = useI18n()

const props = defineProps<{
  prestataire: PrestataireDetail
  displayMode: DisplayMode
}>()

const { prestataire, saveField } = usePrestataire()
const isEdit = computed(() => props.displayMode === 'edit')
/** Bouton back masqué sur les 3 onglets de page-edition (edit/preview/IA), visible uniquement sur la fiche publique. */
const showBack = computed(() => props.displayMode === 'display')
const heroboardOpen = ref(false)
const hasMedia = computed(() => props.prestataire.medias.length > 0)

const emit = defineEmits<{
  openVideo: [youtubeId: string]
  openPhoto: [index: number]
  back: []
}>()

const shareText = computed(() => t('prestataire.share-text', { name: props.prestataire.name }))
</script>

<template>
  <SgiltHero
    :medias="prestataire!.medias"
    :tag="prestataire?.category"
    :title="prestataire?.name ?? ''"
    :show-back="showBack"
    :show-share="!isEdit && hasMedia"
    :share-text="shareText"
    @open-video="emit('openVideo', $event)"
    @open-photo="emit('openPhoto', $event)"
    @back="emit('back')"
  >
    <template #subtitle>
      <EditableText
        as="p"
        v-model="prestataire!.baseline"
        field="baseline"
        :editable="isEdit"
        @commit="saveField('baseline', $event)"
      />
    </template>

    <template #empty-actions>
      <button v-if="isEdit" class="add-media-cta" type="button" @click="heroboardOpen = true">
        <ImageAddIcon />
        {{ $t('prestataire.add-medias-btn') }}
      </button>
    </template>

    <template #top-right>
      <button
        v-if="isEdit && hasMedia"
        class="edit-medias"
        type="button"
        @click="heroboardOpen = true"
      >
        <ImageAddIcon />
        {{ $t('prestataire.edit-medias-btn') }}
      </button>
    </template>
  </SgiltHero>

  <PrestataireMediaDialog
    v-if="prestataire"
    v-model:open="heroboardOpen"
    :prestataire="prestataire"
  />
</template>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// Positionné hors du flux : sa présence ne doit pas augmenter la hauteur du hero.
.add-media-cta {
  position: absolute;
  top: $spacing-m;
  right: $spacing-m;
  display: inline-flex;
  align-items: center;
  gap: $spacing-xs;
  padding: $spacing-s $spacing-l;
  background: $brand-primary;
  color: $text-inverted;
  border: none;
  border-radius: $border-radius-xxl;
  font-family: inherit;
  font-size: $font-size-md;
  font-weight: $font-weight-semibold;
  cursor: pointer;
  white-space: nowrap;
  transition: opacity 150ms ease;

  // Desktop : le texte est centré et plus court, on peut recentrer le bouton verticalement.
  @media (min-width: $breakpoint-desktop) {
    top: 50%;
    right: $spacing-l;
    transform: translateY(-50%);
  }

  &:hover {
    opacity: 0.85;
  }

  svg {
    width: 1.1rem;
    height: 1.1rem;
    flex-shrink: 0;
  }
}

// ─── Bouton édition médias ────────────────────────────────────────────────
.edit-medias {
  position: absolute;
  top: $spacing-m;
  right: $spacing-m;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: $spacing-xs $spacing-m;
  background: rgba(0, 0, 0, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(6px);
  border-radius: 2rem;
  color: #fff;
  font-family: inherit;
  font-size: 0.8rem;
  font-weight: 500;
  white-space: nowrap;
  cursor: pointer;
  transition: background 150ms ease;

  &:hover,
  &:active {
    background: rgba(0, 0, 0, 0.7);
  }

  svg {
    width: 14px;
    height: 14px;
    flex-shrink: 0;
  }
}
</style>
