<template>
  <div class="prestataire-content">
    <!-- CE QUE NOUS PROPOSONS -->
    <section v-if="isEdit || !!prestataire?.offerings.length" class="section">
      <h2 class="title">{{ $t('provider.details.offerings-title') }}</h2>
      <EditableList
        v-model="prestataire!.offerings"
        marker="check"
        field="offerings"
        :display-mode="props.displayMode"
        :new-item="newString"
        :is-empty="isStringEmpty"
      >
        <template #item="{ item, isEditing, update, registerRef }">
          <EditableText
            :ref="registerRef"
            :model-value="item"
            :editable="isEditing"
            field="offerings.item"
            @update:model-value="update($event ?? '')"
          />
        </template>
      </EditableList>
    </section>

    <!-- TOUCHE IDENTITAIRE -->
    <section
      v-if="isEdit || prestataire?.identity.quote || prestataire?.identity.bio"
      class="section identity-spotlight"
    >
      <div class="content">
        <EditableText
          as="blockquote"
          v-model="prestataire!.identity.quote"
          field="identity.quote"
          :editable="isEdit"
          class="quote"
        />
        <EditableText
          as="p"
          v-model="prestataire!.identity.bio"
          field="identity.bio"
          :editable="isEdit"
          class="bio"
        />
      </div>
    </section>

    <!-- BADGES -->
    <section v-if="isEdit || !!prestataire?.badges.length" class="section badges-section">
      <EditableEngagements v-model="prestataire!.badges" :display-mode="props.displayMode" />
    </section>

    <!-- BUDGET (mobile uniquement) -->
    <section v-if="isEdit || prestataire?.budget" class="section budget-section mobile-only">
      <h2 class="title">{{ $t('provider.details.rates') }}</h2>
      <EditableText
        as="p"
        v-model="prestataire!.budget"
        field="budget"
        :editable="isEdit"
        class="budget-text"
      />
    </section>

    <!-- TÉMOIGNAGES -->
    <section v-if="isEdit || !!prestataire?.testimonials.length" class="section">
      <h2 class="title">{{ $t('provider.details.testimonials-title') }}</h2>
      <EditableList
        v-model="prestataire!.testimonials"
        field="testimonials"
        :display-mode="props.displayMode"
        :new-item="newTestimony"
        :is-empty="isTestimonyEmpty"
      >
        <template #ghost-item="{ ghostText }">
          <blockquote class="testimony-ghost">
            <p class="text">{{ ghostText }}</p>
            <footer class="footer">
              <span class="author">{{ t('provider.editable.testimonials.author.ghost') }}</span>
            </footer>
          </blockquote>
        </template>
        <template #item="{ item, isEditing, update, registerRef }">
          <EditableTestimony
            :ref="registerRef"
            :model-value="item"
            :editable="isEditing"
            @update:model-value="update($event)"
          />
        </template>
      </EditableList>
    </section>

    <!-- INFORMATIONS PRATIQUES -->
    <section v-if="isEdit || hasInfosPratiques" class="section infos-section">
      <h2 class="title">{{ $t('provider.details.infos-title') }}</h2>

      <div v-for="group in detailGroups" :key="group.category" class="infos-block">
        <h3 class="title">{{ $t(`provider.details.category.${group.category.toLowerCase()}`) }}</h3>
        <div class="detail-list">
          <div v-for="(item, index) in group.items" :key="index" class="detail-item">
            <p class="detail-content">{{ item.content }}</p>
          </div>
        </div>
      </div>

      <div v-if="isEdit || !!prestataire?.faq.length" class="infos-block">
        <h3 class="title">{{ $t('provider.details.faq-title') }}</h3>
        <EditableList
          v-model="prestataire!.faq"
          field="faq"
          :display-mode="props.displayMode"
          :new-item="newFaqItem"
          :is-empty="isFaqEmpty"
        >
          <template #ghost-item="{ ghostText, ghostIndex }">
            <div class="faq-ghost">
              <p class="question">{{ ghostText }}</p>
              <p class="answer">{{ faqGhostAnswers[ghostIndex] ?? '' }}</p>
            </div>
          </template>
          <template #item="{ item, isEditing, update, registerRef }">
            <EditableFaq
              :ref="registerRef"
              :model-value="item"
              :editable="isEditing"
              @update:model-value="update($event)"
            />
          </template>
        </EditableList>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import EditableEngagements from '~/components/prestataire/EditableEngagements.vue'
import EditableText, {
  newItem as newString,
  isEmpty as isStringEmpty,
} from '~/components/prestataire/EditableText.vue'
import EditableList from '~/components/prestataire/EditableList.vue'
import EditableFaq, {
  newItem as newFaqItem,
  isEmpty as isFaqEmpty,
} from '~/components/prestataire/EditableFaq.vue'
import EditableTestimony, {
  newItem as newTestimony,
  isEmpty as isTestimonyEmpty,
} from '~/components/prestataire/EditableTestimony.vue'
import type { DisplayMode } from '~/types/prestataire'
import { DETAIL_CATEGORY_ORDER } from '~/utils/constants'

const props = defineProps<{
  displayMode: DisplayMode
}>()

const { t, tm, rt } = useI18n()
type RtArg = Parameters<typeof rt>[0]
const faqGhostAnswers = computed<string[]>(() => {
  const raw: unknown = tm('provider.editable.faq.ghost-answers')
  return Array.isArray(raw) ? (raw as RtArg[]).map((item) => rt(item)) : []
})
const { prestataire } = usePrestataire()

const isEdit = computed(() => props.displayMode === 'edit')

const detailGroups = computed(() => {
  const details = prestataire.value?.details ?? []
  return DETAIL_CATEGORY_ORDER.map((category) => ({
    category,
    items: details.filter((detail) => detail.category === category),
  })).filter((group) => group.items.length > 0)
})

const hasInfosPratiques = computed(
  () => !!prestataire.value?.details.length || !!prestataire.value?.faq.length,
)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$content-max-width: 640px;
$section-gap: 2.5rem;

.mobile-only {
  @media (min-width: $breakpoint-desktop) {
    display: none !important;
  }
}

.prestataire-content {
  display: flex;
  flex-direction: column;
  gap: $section-gap;
  padding: $spacing-l $spacing-m;
  max-width: $content-max-width;
  margin: 0 auto;
  width: 100%;

  @media (min-width: $breakpoint-desktop) {
    max-width: none;
    padding: 0;
  }
}

.section {
  display: flex;
  flex-direction: column;
  gap: 1rem;

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

.badges-section {
  position: relative;
}

.badges {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 1rem;

  > * {
    width: 6rem;
  }
}

.identity-spotlight {
  padding: 1.5rem 0;
  border-top: 1px solid #eee;

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
    margin-bottom: 1rem;
    line-height: 1.4;
  }

  .bio {
    font-size: 1rem;
    line-height: 1.6;
    color: $text-secondary;
    margin: 0;
  }
}

.budget-text {
  font-size: 0.95rem;
  color: $text-secondary;
  line-height: 1.6;
  margin: 0;
}

.infos-section {
  gap: 1.5rem;
}

.faq-ghost {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;

  .question {
    font-size: 0.9rem;
    font-weight: 600;
    color: $color-primary;
    margin: 0;
  }

  .answer {
    font-size: 0.9rem;
    line-height: 1.6;
    color: $text-secondary;
    margin: 0;
  }
}

.testimony-ghost {
  margin: 0;
  padding: $spacing-m;
  background: #fafafa;
  border-radius: $radius-md;
  border: 1px solid rgba(0, 0, 0, 0.06);

  .text {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.05rem;
    font-style: italic;
    line-height: 1.6;
    color: $color-primary;
    margin: 0 0 0.75rem;
  }

  .footer {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .author {
    font-size: 0.85rem;
    font-weight: 600;
    color: $text-secondary;
  }
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

  .detail-item {
    display: flex;
    flex-direction: column;
    gap: 0.3rem;
  }

  .detail-content {
    font-size: 0.9rem;
    line-height: 1.6;
    color: $text-secondary;
    margin: 0;
  }
}
</style>
