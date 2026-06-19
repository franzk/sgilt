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
      />
    </section>

    <!-- TOUCHE IDENTITAIRE -->
    <section v-if="isEdit || prestataire?.identity.quote || prestataire?.identity.bio" class="section identity-spotlight">
      <div class="content">
        <EditableText as="blockquote" v-model="prestataire!.identity.quote" field="identity.quote" :editable="isEdit" class="quote" />
        <EditableText as="p" v-model="prestataire!.identity.bio" field="identity.bio" :editable="isEdit" class="bio" />
      </div>
    </section>

    <!-- BADGES -->
    <section v-if="prestataire?.badges.length > 0" class="section badges-section">
      <div class="badges">
        <EngagementBadge
          v-for="badge in prestataire?.badges"
          :key="badge.label"
          :badge="badge"
        />
      </div>
    </section>

    <!-- BUDGET (mobile uniquement) -->
    <section v-if="prestataire?.budget" class="section budget-section mobile-only">
      <h2 class="title">{{ $t('provider.details.rates') }}</h2>
      <EditableText as="p" v-model="prestataire!.budget" field="budget" :editable="isEdit" class="budget-text" />
    </section>

    <!-- TÉMOIGNAGES -->
    <section v-if="!!prestataire?.testimonials.length" class="section">
      <h2 class="title">{{ $t('provider.details.testimonials-title') }}</h2>
      <div class="testimonials">
        <blockquote v-for="t in prestataire?.testimonials" :key="t.author" class="testimonial">
          <p class="text">« {{ t.text }} »</p>
          <footer class="footer">
            <span class="author">{{ t.author }}</span>
            <span v-if="t.eventType" class="event">{{ t.eventType }}</span>
          </footer>
        </blockquote>
      </div>
    </section>

    <!-- INFORMATIONS PRATIQUES -->
    <section v-if="isEdit || hasInfosPratiques" class="section infos-section">
      <h2 class="title">{{ $t('provider.details.infos-title') }}</h2>
      <div
        v-if="isEdit || !!prestataire?.logistics.length"
        class="infos-block"
      >
        <h3 class="title">{{ $t('provider.details.logistics-title') }}</h3>
        <EditableList
          v-model="prestataire!.logistics"
          marker="dash"
          field="logistics"
          :display-mode="props.displayMode"
        />
      </div>
      <div
        v-if="isEdit || !!prestataire?.technical.length"
        class="infos-block"
      >
        <h3 class="title">{{ $t('provider.details.technical-title') }}</h3>
        <EditableList
          v-model="prestataire!.technical"
          marker="dash"
          field="technical"
          :display-mode="props.displayMode"
        />
      </div>
      <div v-if="!!prestataire?.faq.length" class="infos-block">
        <h3 class="title">{{ $t('provider.details.faq-title') }}</h3>
        <div class="faq">
          <div v-for="item in prestataire?.faq" :key="item.question" class="faq-item">
            <p class="question">{{ item.question }}</p>
            <p class="answer">{{ item.answer }}</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import EngagementBadge from '~/components/prestataire/EngagementBadge.vue'
import EditableText from '~/components/prestataire/EditableText.vue'
import EditableList from '~/components/prestataire/EditableList.vue'
import type { DisplayMode } from '~/types/prestataire'

const props = defineProps<{
  displayMode: DisplayMode
}>()

const { prestataire } = usePrestataire()

const isEdit = computed(() => props.displayMode === 'edit')

const hasInfosPratiques = computed(
  () =>
    !!prestataire.value?.logistics.length ||
    !!prestataire.value?.technical.length ||
    !!prestataire.value?.faq.length,
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

.testimonials {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.testimonial {
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

  .event {
    font-size: 0.8rem;
    color: $text-secondary;
    opacity: 0.6;

    &::before {
      content: '· ';
    }
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
}

.faq {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.faq-item {
  .question {
    font-size: 0.9rem;
    font-weight: 600;
    color: $color-primary;
    margin: 0 0 0.3rem;
  }

  .answer {
    font-size: 0.9rem;
    line-height: 1.6;
    color: $text-secondary;
    margin: 0;
  }
}
</style>
