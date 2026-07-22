<template>
  <section v-if="isEdit || prestataire?.shortDescription" class="card-preview-section">
    <h2 class="title">{{ $t('provider.details.card-preview-title') }}</h2>
    <PrestataireCard :provider="cardPreview" selectable class="card-preview">
      <template v-if="isEdit" #description>
        <EditableText
          as="div"
          v-model="prestataire!.shortDescription"
          field="shortDescription"
          :editable="isEdit"
          @commit="saveField('shortDescription', $event)"
        />
      </template>
    </PrestataireCard>
  </section>
</template>

<script setup lang="ts">
import PrestataireCard from '~/components/cards/PrestataireCard.vue'
import EditableText from '~/components/prestataire/EditableText.vue'
import { mapPrestataireDetailToCard } from '~/data/prestataire/mapper/prestataireMapper'
import type { PrestataireCardDetail } from '~/data/prestataire/domain/PrestataireCardDetail'
import type { DisplayMode } from '~/types/prestataire'

const props = defineProps<{
  displayMode: DisplayMode
}>()

const { prestataire, saveField } = usePrestataire()

const isEdit = computed(() => props.displayMode === 'edit')

const cardPreview = computed<PrestataireCardDetail | undefined>(() =>
  prestataire.value ? mapPrestataireDetailToCard(prestataire.value) : undefined,
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.card-preview-section {
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

.card-preview {
  width: 100%;
  max-width: 220px;
}
</style>
