<template>
  <div class="prestataire-content">
    <!-- CE QUE NOUS PROPOSONS -->
    <section v-if="prestataire?.offerings.length > 0" class="section">
      <h2 class="title">{{ $t('provider.details.offerings-title') }}</h2>
      <ul class="offerings">
        <li v-for="item in prestataire?.offerings" :key="item" class="offering-item">
          {{ item }}
        </li>
      </ul>
    </section>

    <!-- TOUCHE IDENTITAIRE -->
    <section v-if="prestataire?.identity" class="section identity-spotlight">
      <div class="content">
        <EditableText as="blockquote" v-model="prestataire!.identity!.quote" field="identity.quote" :editable="true" class="quote" />
        <EditableText as="p" v-model="prestataire!.identity!.bio" field="identity.bio" :editable="true" class="bio" />
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
      <p class="budget-text">{{ prestataire?.budget }}</p>
    </section>

    <!-- TÉMOIGNAGES -->
    <section
      v-if="prestataire?.testimonials && prestataire.testimonials.length > 0"
      class="section"
    >
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
    <section v-if="hasInfosPratiques" class="section infos-section">
      <h2 class="title">{{ $t('provider.details.infos-title') }}</h2>
      <div
        v-if="prestataire?.logistics && prestataire.logistics.length > 0"
        class="infos-block"
      >
        <h3 class="title">{{ $t('provider.details.logistics-title') }}</h3>
        <ul class="infos-list">
          <li v-for="item in prestataire?.logistics" :key="item">{{ item }}</li>
        </ul>
      </div>
      <div
        v-if="prestataire?.technical && prestataire.technical.length > 0"
        class="infos-block"
      >
        <h3 class="title">{{ $t('provider.details.technical-title') }}</h3>
        <ul class="infos-list">
          <li v-for="item in prestataire?.technical" :key="item">{{ item }}</li>
        </ul>
      </div>
      <div v-if="prestataire?.faq && prestataire.faq.length > 0" class="infos-block">
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

const { prestataire } = usePrestataire()

const hasInfosPratiques = computed(
  () =>
    (prestataire.value?.logistics?.length ?? 0) > 0 ||
    (prestataire.value?.technical?.length ?? 0) > 0 ||
    (prestataire.value?.faq?.length ?? 0) > 0,
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

.offerings {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

$color-success: #2e7d32;

.offering-item {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
  font-size: 0.95rem;
  line-height: 1.35;
  color: $text-secondary;

  &::before {
    content: '';
    flex-shrink: 0;
    width: 16px;
    height: 16px;
    margin-top: 0.2rem;
    background-color: $color-success;
    -webkit-mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='black' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='20 6 9 17 4 12'%3E%3C/polyline%3E%3C/svg%3E");
    mask-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='black' stroke-width='3' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='20 6 9 17 4 12'%3E%3C/polyline%3E%3C/svg%3E");
    mask-repeat: no-repeat;
    mask-size: contain;
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

.infos-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.4rem;

  li {
    font-size: 0.9rem;
    line-height: 1.5;
    color: $text-secondary;
    padding-left: 1rem;
    position: relative;

    &::before {
      content: '–';
      position: absolute;
      left: 0;
      opacity: 0.4;
    }
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
