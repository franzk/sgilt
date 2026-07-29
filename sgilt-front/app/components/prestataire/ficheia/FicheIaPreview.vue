<template>
  <div class="ia-preview">
    <!-- CE QUE NOUS PROPOSONS -->
    <section v-if="content.offerings.length > 0" class="section">
      <div class="section-header">
        <h2 class="title">{{ $t('provider.details.offerings-title') }}</h2>
        <SgiltButton variant="secondary" @click="$emit('apply', 'OFFERINGS')">
          {{ $t('provider.edit.ia.apply-field') }}
        </SgiltButton>
      </div>
      <EditableList
        :model-value="content.offerings"
        marker="check"
        field="offerings"
        display-mode="preview"
        :new-item="newString"
        :is-empty="isStringEmpty"
        @update:model-value="() => {}"
      >
        <template #item="{ item }">
          <EditableText :model-value="item" field="offerings.item" />
        </template>
      </EditableList>
    </section>

    <!-- TOUCHE IDENTITAIRE -->
    <section v-if="content.identity.quote || content.identity.bio" class="section identity-spotlight">
      <div class="section-header">
        <h2 class="title">{{ $t('provider.edit.ia.field.identity') }}</h2>
        <SgiltButton variant="secondary" @click="$emit('apply', 'IDENTITY')">
          {{ $t('provider.edit.ia.apply-field') }}
        </SgiltButton>
      </div>
      <div class="content">
        <blockquote class="quote">{{ content.identity.quote }}</blockquote>
        <p class="bio">{{ content.identity.bio }}</p>
      </div>
    </section>

    <!-- TÉMOIGNAGES -->
    <section v-if="content.testimonials.length > 0" class="section">
      <div class="section-header">
        <h2 class="title">{{ $t('provider.details.testimonials-title') }}</h2>
        <SgiltButton variant="secondary" @click="$emit('apply', 'TESTIMONIALS')">
          {{ $t('provider.edit.ia.apply-field') }}
        </SgiltButton>
      </div>
      <EditableList
        :model-value="content.testimonials"
        field="testimonials"
        display-mode="preview"
        :new-item="newTestimony"
        :is-empty="isTestimonyEmpty"
        @update:model-value="() => {}"
      >
        <template #item="{ item }">
          <EditableTestimony :model-value="item" @update:model-value="() => {}" />
        </template>
      </EditableList>
    </section>

    <!-- INFOS PRATIQUES -->
    <section v-if="detailGroups.length > 0" class="section infos-section">
      <div class="section-header">
        <h2 class="title">{{ $t('provider.edit.ia.block.details') }}</h2>
        <SgiltButton variant="secondary" @click="$emit('apply', 'DETAILS')">
          {{ $t('provider.edit.ia.apply-field') }}
        </SgiltButton>
      </div>
      <div v-for="group in detailGroups" :key="group.category" class="infos-block">
        <h3 class="title">{{ $t(`provider.details.category.${group.category.toLowerCase()}`) }}</h3>
        <div class="detail-list">
          <p v-for="(item, i) in group.items" :key="i" class="detail-content">{{ item.content }}</p>
        </div>
      </div>
    </section>

    <!-- FAQ -->
    <section v-if="content.faq.length > 0" class="section infos-section">
      <div class="section-header">
        <h2 class="title">{{ $t('provider.edit.ia.block.faq') }}</h2>
        <SgiltButton variant="secondary" @click="$emit('apply', 'FAQ')">
          {{ $t('provider.edit.ia.apply-field') }}
        </SgiltButton>
      </div>
      <EditableList
        :model-value="content.faq"
        field="faq"
        display-mode="preview"
        :new-item="newFaqItem"
        :is-empty="isFaqEmpty"
        @update:model-value="() => {}"
      >
        <template #item="{ item }">
          <EditableFaq :model-value="item" @update:model-value="() => {}" />
        </template>
      </EditableList>
    </section>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import EditableList from '~/components/prestataire/EditableList.vue'
import EditableText, {
  newItem as newString,
  isEmpty as isStringEmpty,
} from '~/components/prestataire/EditableText.vue'
import EditableFaq, {
  newItem as newFaqItem,
  isEmpty as isFaqEmpty,
} from '~/components/prestataire/EditableFaq.vue'
import EditableTestimony, {
  newItem as newTestimony,
  isEmpty as isTestimonyEmpty,
} from '~/components/prestataire/EditableTestimony.vue'
import { DETAIL_CATEGORY_ORDER } from '~/utils/constants'
import type { FicheIaGenerationContentDto } from '~/data/ficheia/dto/FicheIaGenerationDto'
import type { FicheIaSection } from '~/data/ficheia/dto/FicheIaApplyDto'

const props = defineProps<{
  content: FicheIaGenerationContentDto
}>()

defineEmits<{ apply: [section: FicheIaSection] }>()

const detailGroups = computed(() =>
  DETAIL_CATEGORY_ORDER.map((category) => ({
    category,
    items: props.content.details.filter((detail) => detail.category === category),
  })).filter((group) => group.items.length > 0),
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.ia-preview {
  display: flex;
  flex-direction: column;
  gap: 2.5rem;
}

.section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $spacing-m;
  flex-wrap: wrap;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);

  .title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.4rem;
    font-weight: 600;
    color: $color-primary;
    margin: 0;
  }
}

.identity-spotlight {
  .content {
    border-left: 4px solid $color-accent;
    padding-left: 1.2rem;
  }

  .quote {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.2rem;
    font-style: italic;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 1rem;
    line-height: 1.4;
  }

  .bio {
    font-size: 1rem;
    line-height: 1.6;
    color: $text-secondary;
    margin: 0;
    white-space: pre-line;
  }
}

.infos-section {
  gap: 1.5rem;
}

.infos-block {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;

  .title {
    font-size: 0.8rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    color: $text-secondary;
    opacity: 0.6;
    margin: 0;
  }

  .detail-list {
    display: flex;
    flex-direction: column;
    gap: 0.6rem;
  }

  .detail-content {
    font-size: 0.9rem;
    line-height: 1.6;
    color: $text-secondary;
    margin: 0;
  }
}
</style>
