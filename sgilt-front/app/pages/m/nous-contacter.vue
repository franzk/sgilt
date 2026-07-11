<template>
  <div class="contact-page">
    <div class="content">
      <p class="eyebrow">{{ $t('contact.page.eyebrow') }}</p>
      <h1 class="title">{{ $t('contact.page.title') }}</h1>
      <p class="description">{{ $t('contact.page.description') }}</p>
      <a class="button" href="mailto:contact@sgilt.alsace">
        <MailIcon class="button-icon" />
        {{ $t('contact.page.button') }}
      </a>
      <div class="email">
        <a href="mailto:contact@sgilt.alsace">contact@sgilt.alsace</a>
        <button
          class="copy"
          type="button"
          :class="{ copied }"
          :aria-label="copied ? $t('contact.page.copied-aria') : $t('contact.page.copy-email-aria')"
          @click="copyEmail"
        >
          <CheckIcon v-if="copied" class="copy-icon" />
          <FileCopyIcon v-else class="copy-icon" />
        </button>
      </div>
    </div>

    <div class="card">
      <h2>{{ $t('contact.page.card-title') }}</h2>
      <ul>
        <li>{{ $t('contact.page.card-item-discover') }}</li>
        <li>{{ $t('contact.page.card-item-become-provider') }}</li>
        <li>{{ $t('contact.page.card-item-event') }}</li>
        <li>{{ $t('contact.page.card-item-idea') }}</li>
      </ul>
      <p class="delay">{{ $t('contact.page.delay') }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { CheckIcon, FileCopyIcon, MailIcon } from '@remixicons/vue/line'

useHead({ title: 'Nous contacter' })

const copied = ref(false)

async function copyEmail() {
  await navigator.clipboard.writeText('contact@sgilt.alsace')
  copied.value = true
  setTimeout(() => (copied.value = false), 2000)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.contact-page {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(320px, 0.85fr);
  gap: clamp(3rem, 8vw, 8rem);
  align-items: center;
  max-width: $container-max-width;
  min-height: calc(100vh - #{$header-height});
  margin: 0 auto;
  padding: clamp(4rem, 8vw, 8rem) clamp($spacing-l, 5vw, 5rem);

  @media (max-width: $breakpoint-desktop) {
    grid-template-columns: 1fr;
    align-items: start;
    min-height: auto;
  }
}

// ── Contenu ────────────────────────────────────────────────────────────────────

.content {
  max-width: 720px;

  .eyebrow {
    margin-bottom: $spacing-l;
    font-size: $font-size-xs;
    font-weight: $font-weight-bold;
    letter-spacing: 0.28em;
    text-transform: uppercase;
    color: $brand-accent;
  }

  .title {
    margin: 0 0 $spacing-l;
    font-family: 'Cormorant Garamond', serif;
    font-size: clamp(1.75rem, 4vw, #{$font-size-3xl});
    font-weight: $font-weight-regular;
    line-height: 1.05;
    color: $text-primary;
  }

  .description {
    max-width: 500px;
    margin-bottom: $spacing-xl;
    font-size: $font-size-md;
    line-height: $line-height-relaxed;
    color: $text-secondary;
  }

  .button {
    display: inline-flex;
    align-items: center;
    gap: $spacing-s;
    padding: $spacing-m $spacing-l;
    border-radius: 9999px;
    background: $brand-accent;
    color: $brand-primary;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    text-decoration: none;
    transition:
      transform 160ms ease,
      box-shadow 160ms ease;

    .button-icon {
      width: 1rem;
      height: 1rem;
    }

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 12px 28px rgba($brand-accent, 0.28);
    }

    &:focus-visible {
      outline: 3px solid $brand-primary;
      outline-offset: 4px;
    }
  }

  .email {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    width: fit-content;
    margin-top: $spacing-l;

    a {
      font-size: $font-size-sm;
      color: $text-secondary;
      text-underline-offset: 0.25em;
    }

    .copy {
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      width: 1.6rem;
      height: 1.6rem;
      padding: 0;
      border: 1px solid $divider-color;
      border-radius: $radius-sm;
      background: none;
      color: $text-secondary;
      cursor: pointer;
      transition:
        background 120ms ease,
        color 120ms ease;

      &.copied {
        color: $state-success;
        border-color: $state-success;
      }

      &:hover {
        background: $surface-soft;
      }
    }

    .copy-icon {
      width: 0.85rem;
      height: 0.85rem;
    }
  }
}

// ── Carte ──────────────────────────────────────────────────────────────────────

.card {
  padding: clamp($spacing-xl, 4vw, $spacing-xxl);
  border: 1px solid rgba($text-primary, 0.08);
  border-radius: $radius-xl;
  background: $brand-subtle;
  box-shadow: 0 24px 70px $shadow-m;

  h2 {
    margin: 0 0 $spacing-l;
    font-family: 'Cormorant Garamond', serif;
    font-size: $font-size-xl;
    font-weight: $font-weight-regular;
    color: $text-primary;
  }

  ul {
    display: grid;
    gap: $spacing-m;
    margin: 0;
    padding: 0;
    list-style: none;
  }

  li {
    display: flex;
    gap: $spacing-s;
    font-size: $font-size-sm;
    line-height: $line-height-normal;
    color: $text-primary;

    &::before {
      content: '✓';
      color: $state-warning;
      font-weight: $font-weight-bold;
    }
  }

  .delay {
    margin: $spacing-xl 0 0;
    padding-top: $spacing-l;
    border-top: 1px solid rgba($text-primary, 0.1);
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    color: $text-primary;
  }
}
</style>
