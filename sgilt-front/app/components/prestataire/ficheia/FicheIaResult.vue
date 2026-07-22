<template>
  <div class="ia-result">
    <SgiltButton class="bulk-btn" @click="overwriteConfirmOpen = true">
      {{ $t('provider.edit.ia.bulk-overwrite') }}
    </SgiltButton>
    <p v-if="applyError" class="error">{{ $t('provider.edit.ia.apply-error') }}</p>

    <SgiltConfirmDialog
      v-model:open="overwriteConfirmOpen"
      :title="$t('provider.edit.ia.bulk-overwrite-dialog.title')"
      :message="$t('provider.edit.ia.bulk-overwrite-dialog.message')"
      :confirm-label="$t('provider.edit.ia.bulk-overwrite-dialog.confirm')"
      :cancel-label="$t('provider.edit.ia.bulk-overwrite-dialog.cancel')"
      :confirm-loading="applying === 'ALL'"
      destructive
      max-width="480px"
      @confirm="applyAll"
    />

    <div v-if="result" class="blocks">
      <FicheIaCollapsibleBlock
        :title="$t('provider.edit.ia.block.presentation')"
        :default-open="justGenerated"
      >
        <FicheIaFieldRow
          :label="$t('provider.edit.ia.field.baseline')"
          section="BASELINE"
          :is-list="false"
          :has-existing-content="false"
          :applying-key="applying"
          @apply="onApply"
        >
          <p>{{ result.baseline }}</p>
        </FicheIaFieldRow>

        <FicheIaFieldRow
          :label="$t('provider.edit.ia.field.identity')"
          section="IDENTITY"
          :is-list="false"
          :has-existing-content="false"
          :applying-key="applying"
          @apply="onApply"
        >
          <blockquote>{{ result.identity.quote }}</blockquote>
          <p>{{ result.identity.bio }}</p>
        </FicheIaFieldRow>

        <FicheIaFieldRow
          :label="$t('provider.edit.ia.field.offerings')"
          section="OFFERINGS"
          :is-list="true"
          :has-existing-content="(prestataire?.offerings.length ?? 0) > 0"
          :applying-key="applying"
          @apply="onApply"
        >
          <ul>
            <li v-for="(offering, i) in result.offerings" :key="i">{{ offering }}</li>
          </ul>
        </FicheIaFieldRow>
      </FicheIaCollapsibleBlock>

      <FicheIaCollapsibleBlock
        :title="$t('provider.edit.ia.block.testimonials')"
        :default-open="justGenerated"
      >
        <FicheIaFieldRow
          :label="$t('provider.edit.ia.block.testimonials')"
          section="TESTIMONIALS"
          :is-list="true"
          :has-existing-content="(prestataire?.testimonials.length ?? 0) > 0"
          :applying-key="applying"
          @apply="onApply"
        >
          <div v-for="(testimonial, i) in result.testimonials" :key="i" class="testimonial">
            <p class="text">{{ testimonial.text }}</p>
            <p class="author">{{ testimonial.author }}</p>
          </div>
        </FicheIaFieldRow>
      </FicheIaCollapsibleBlock>

      <FicheIaCollapsibleBlock
        :title="$t('provider.edit.ia.block.details')"
        :default-open="justGenerated"
      >
        <FicheIaFieldRow
          :label="$t('provider.edit.ia.block.details')"
          section="DETAILS"
          :is-list="true"
          :has-existing-content="(prestataire?.details.length ?? 0) > 0"
          :applying-key="applying"
          @apply="onApply"
        >
          <div v-for="group in iaDetailGroups" :key="group.category" class="detail-group">
            <h4>{{ $t(`provider.details.category.${group.category.toLowerCase()}`) }}</h4>
            <ul>
              <li v-for="(item, i) in group.items" :key="i">{{ item.content }}</li>
            </ul>
          </div>
        </FicheIaFieldRow>
      </FicheIaCollapsibleBlock>

      <FicheIaCollapsibleBlock
        :title="$t('provider.edit.ia.block.faq')"
        :default-open="justGenerated"
      >
        <FicheIaFieldRow
          :label="$t('provider.edit.ia.block.faq')"
          section="FAQ"
          :is-list="true"
          :has-existing-content="(prestataire?.faq.length ?? 0) > 0"
          :applying-key="applying"
          @apply="onApply"
        >
          <div v-for="(item, i) in result.faq" :key="i" class="faq-item">
            <p class="question">{{ item.question }}</p>
            <p class="answer">{{ item.answer }}</p>
          </div>
        </FicheIaFieldRow>
      </FicheIaCollapsibleBlock>

      <FicheIaCollapsibleBlock
        :title="$t('provider.edit.ia.block.budget')"
        :default-open="justGenerated"
      >
        <FicheIaFieldRow
          :label="$t('provider.edit.ia.field.budget')"
          section="BUDGET"
          :is-list="false"
          :has-existing-content="false"
          :applying-key="applying"
          @apply="onApply"
        >
          <p>{{ result.budget }}</p>
        </FicheIaFieldRow>
      </FicheIaCollapsibleBlock>
    </div>

    <!-- APERÇU CARD RECHERCHE : même card que la fiche réelle, légende remplacée par celle générée -->
    <div v-if="cardPreview" class="card-preview-section">
      <p class="label">{{ $t('provider.edit.ia.card-preview-title') }}</p>
      <PrestataireCard :provider="cardPreview" selectable class="card-preview" />
      <SgiltButton
        variant="secondary"
        :loading="applying === 'SHORT_DESCRIPTION:REMPLACER'"
        @click="onApply('SHORT_DESCRIPTION', 'REMPLACER')"
      >
        {{ $t('provider.edit.ia.card-preview-copy') }}
      </SgiltButton>
    </div>

    <div class="relaunch">
      <button
        v-if="triesLeft && triesLeft > 0"
        type="button"
        class="link"
        @click="$emit('relaunch')"
      >
        {{ $t('provider.edit.ia.relaunch-link', { count: triesLeft }, triesLeft ?? 0) }}
      </button>
      <p v-else class="quota-exhausted">{{ $t('provider.edit.ia.error-quota') }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import SgiltConfirmDialog from '~/components/basics/dialogs/SgiltConfirmDialog.vue'
import FicheIaCollapsibleBlock from './FicheIaCollapsibleBlock.vue'
import FicheIaFieldRow from './FicheIaFieldRow.vue'
import PrestataireCard from '~/components/cards/PrestataireCard.vue'
import { mapPrestataireDetailToCard } from '~/data/prestataire/mapper/prestataireMapper'
import { DETAIL_CATEGORY_ORDER } from '~/utils/constants'
import type { PrestataireCardDetail } from '~/data/prestataire/domain/PrestataireCardDetail'
import type { FicheIaSection, FicheIaAction } from '~/data/ficheia/dto/FicheIaApplyDto'

defineEmits<{ relaunch: [] }>()

const { result, triesLeft, justGenerated, applying, applyError, applyOne, applyAll } = useFicheIa()
const { prestataire } = usePrestataire()

const overwriteConfirmOpen = ref(false)

/** Card réelle (photo, nom, catégorie), légende remplacée par celle proposée par l'IA. */
const cardPreview = computed<PrestataireCardDetail | undefined>(() => {
  if (!prestataire.value || !result.value) return undefined
  return {
    ...mapPrestataireDetailToCard(prestataire.value),
    shortDescription: result.value.shortDescription,
  }
})

const iaDetailGroups = computed(() =>
  DETAIL_CATEGORY_ORDER.map((category) => ({
    category,
    items: (result.value?.details ?? []).filter((d) => d.category === category),
  })).filter((group) => group.items.length > 0),
)

function onApply(section: FicheIaSection, action: FicheIaAction): void {
  applyOne(section, action)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.ia-result {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.relaunch {
  .link {
    background: none;
    border: none;
    padding: 0;
    font-family: inherit;
    font-size: $font-size-sm;
    color: $color-primary;
    text-decoration: underline;
    cursor: pointer;
  }

  .quota-exhausted {
    font-size: $font-size-sm;
    color: $text-secondary;
    margin: 0;
  }
}

.bulk-btn {
  align-self: flex-start;
}

.error {
  font-size: $font-size-sm;
  color: $state-error;
  margin: 0;
}

.blocks {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
}

.card-preview-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-s;

  .label {
    font-size: 0.85rem;
    font-weight: 600;
    color: $text-secondary;
    margin: 0;
  }
}

.card-preview {
  width: 100%;
  max-width: 220px;
}

.testimonial,
.faq-item {
  & + & {
    margin-top: $spacing-s;
  }
}

.testimonial .author,
.faq-item .question {
  font-weight: $font-weight-medium;
}

.detail-group {
  & + & {
    margin-top: $spacing-s;
  }

  h4 {
    font-size: 0.85rem;
    font-weight: 600;
    color: $text-secondary;
    margin: 0 0 $spacing-xxs;
  }

  ul {
    margin: 0;
    padding-left: 1.2rem;
  }
}
</style>
