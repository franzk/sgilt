<template>
  <section v-if="isEdit || displayMode === 'preview'" class="google-preview-section">
    <h2 class="title">{{ $t('provider.details.google-preview-title') }}</h2>

    <div class="google-result">
      <div class="breadcrumb">
        <span class="favicon" aria-hidden="true" />
        <span class="url">{{ host }} › {{ prestataire?.slug }}</span>
      </div>
      <EditableText
        as="div"
        v-model="prestataire!.metaTitle"
        :display-value="truncate(prestataire?.metaTitle, TITLE_LIMIT)"
        field="metaTitle"
        :editable="isEdit"
        class="result-title"
        @commit="saveField('metaTitle', $event)"
      />
      <EditableText
        as="div"
        v-model="prestataire!.metaDescription"
        :display-value="truncate(prestataire?.metaDescription, DESCRIPTION_LIMIT)"
        field="metaDescription"
        :editable="isEdit"
        multiline
        class="result-description"
        @commit="saveField('metaDescription', $event)"
      />
    </div>

    <p v-if="isEdit && !isIndexable" class="noindex-warning">
      {{ $t('provider.details.google-preview-noindex-warning') }}
    </p>
  </section>
</template>

<script setup lang="ts">
import EditableText from '~/components/prestataire/EditableText.vue'
import type { DisplayMode } from '~/types/prestataire'

const props = defineProps<{
  displayMode: DisplayMode
}>()

const { prestataire, saveField } = usePrestataire()

const isEdit = computed(() => props.displayMode === 'edit')
const isIndexable = computed(() => !!prestataire.value?.metaTitle && !!prestataire.value?.metaDescription)

const site = useSiteConfig()
const host = computed(() => site.url.replace(/^https?:\/\//, ''))

/**
 * Longueurs approximatives avant troncature par Google (titre ~60 caractères, description ~155).
 * Tronqué par nombre de caractères plutôt qu'en CSS : la largeur d'affichage varie avec l'écran,
 * pas la limite réelle de Google.
 */
const TITLE_LIMIT = 60
const DESCRIPTION_LIMIT = 155

function truncate(value: string | null | undefined, limit: number): string | null {
  if (!value) return null
  return value.length > limit ? `${value.slice(0, limit).trimEnd()}…` : value
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.google-preview-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: $spacing-l $spacing-m;
  max-width: 640px;
  margin: 0 auto;
  width: 100%;

  @media (min-width: $breakpoint-desktop) {
    max-width: 1280px;
    padding: 0 2.5rem 2.5rem;
  }

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.4rem;
    font-weight: 600;
    color: $color-primary;
    margin: 0;
    padding-bottom: 0.5rem;
    border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  }
}

.google-result {
  width: 100%;
  max-width: 560px;
  padding: 1.25rem 1.5rem;
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: $radius-md;
  font-family: Arial, sans-serif;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.3rem;

  .favicon {
    width: 18px;
    height: 18px;
    border-radius: 50%;
    background: $color-primary;
    flex-shrink: 0;
  }

  .url {
    font-size: 0.8rem;
    color: #202124;
  }
}

.result-title {
  font-size: 1.25rem;
  line-height: 1.3;
  color: #1a0dab;
  margin-bottom: 0.2rem;

  // Filet de sécurité : la troncature réelle est déjà faite par caractères (voir `truncate()`
  // dans le script), indépendante de la largeur d'écran.
  :deep(span) {
    overflow-wrap: anywhere;
  }
}

.result-description {
  font-size: 0.875rem;
  line-height: 1.4;
  color: #4d5156;

  :deep(span) {
    overflow-wrap: anywhere;
  }
}

.noindex-warning {
  width: 100%;
  max-width: 560px;
  margin: 0;
  font-size: 0.8rem;
  color: $state-warning;
}
</style>