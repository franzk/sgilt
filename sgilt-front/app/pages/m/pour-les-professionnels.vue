<template>
  <div class="pro-landing">
    <section class="hero">
      <div class="wrap">
        <p class="eyebrow">{{ $t('pro-landing.page.eyebrow') }}</p>
        <h1>{{ $t('pro-landing.page.title') }}</h1>
        <p class="lede">{{ $t('pro-landing.page.lede') }}</p>
      </div>
    </section>

    <div class="wrap content">
      <ul class="benefits">
        <li>
          <h2>{{ $t('pro-landing.page.benefit-1-title') }}</h2>
          <p>{{ $t('pro-landing.page.benefit-1-description') }}</p>
        </li>
        <li>
          <h2>{{ $t('pro-landing.page.benefit-2-title') }}</h2>
          <p>{{ $t('pro-landing.page.benefit-2-description') }}</p>
        </li>
        <li>
          <h2>{{ $t('pro-landing.page.benefit-3-title') }}</h2>
          <p>{{ $t('pro-landing.page.benefit-3-description') }}</p>
        </li>
      </ul>
    </div>

    <section class="how">
      <div class="wrap">
        <h2>{{ $t('pro-landing.page.how-title') }}</h2>
        <i18n-t keypath="pro-landing.page.how-instructions" tag="p" scope="global">
          <template #email>
            <a href="mailto:contact@sgilt.alsace">contact@sgilt.alsace</a>
          </template>
        </i18n-t>
        <p class="callback">{{ $t('pro-landing.page.how-callback') }}</p>
      </div>
    </section>

    <div class="wrap footer">
      <p class="already-account">
        {{ $t('pro-landing.page.already-account') }}
        <button type="button" class="login-link" @click="handleLogin">
          {{ $t('pro-landing.page.login') }}
        </button>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useKeycloak } from '~/composables/useKeycloak'

useHead({ title: 'Pour les professionnels - Sgilt' })

const { login } = useKeycloak()

function handleLogin() {
  login({ redirectUri: window.location.origin + '/auth/redirect' })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.wrap {
  max-width: $container-max-width;
  margin: 0 auto;
  padding: 0 $section-padding-x;
}

// ── Hero ───────────────────────────────────────────────────────────────────────

.hero {
  border-bottom: $border-width-s solid $divider-color;
  padding: $spacing-xxxl 0 $spacing-xxl;

  .eyebrow {
    font-size: $font-size-xs;
    letter-spacing: 0.22em;
    text-transform: uppercase;
    color: $brand-accent;
    font-weight: $font-weight-semibold;
    margin-bottom: $spacing-m;
  }

  h1 {
    font-family: 'Cormorant Garamond', serif;
    font-weight: $font-weight-medium;
    font-size: clamp(2.2rem, 6vw, 3.8rem);
    line-height: 1.05;
    letter-spacing: -0.01em;
    max-width: 16ch;
    margin: 0;
  }

  .lede {
    margin: $spacing-l 0 0;
    font-size: $font-size-lg;
    color: $text-secondary;
    max-width: 58ch;
  }
}

// ── Bénéfices ──────────────────────────────────────────────────────────────────

.content {
  padding: $spacing-xxxl 0;
}

.benefits {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  grid-template-columns: 1fr;
  gap: $spacing-xl;
  max-width: 640px;

  @media (min-width: $breakpoint-desktop) {
    grid-template-columns: repeat(3, 1fr);
    max-width: none;
  }

  h2 {
    margin: 0 0 $spacing-xs;
    font-family: 'Cormorant Garamond', serif;
    font-size: $font-size-xl;
    font-weight: $font-weight-medium;
    color: $text-primary;
  }

  p {
    margin: 0;
    font-size: $font-size-md;
    line-height: $line-height-relaxed;
    color: $text-secondary;
  }
}

// ── Comment ça se passe ? ────────────────────────────────────────────────────────

.how {
  border-top: $border-width-s solid $divider-color;
  padding: $spacing-xxl 0 $spacing-xxxl;

  h2 {
    margin: 0 0 $spacing-m;
    font-family: 'Cormorant Garamond', serif;
    font-size: $font-size-xl;
    font-weight: $font-weight-medium;
    color: $text-primary;
  }

  p {
    max-width: 58ch;
    margin: 0 0 $spacing-s;
    font-size: $font-size-md;
    line-height: $line-height-relaxed;
    color: $text-secondary;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .callback {
    margin-top: $spacing-m;
    font-size: $font-size-sm;
    color: $text-secondary;
    opacity: 0.8;
  }

  a {
    color: $text-primary;
    text-decoration: underline;
    text-underline-offset: 0.25em;
  }
}

// ── Pied de page : déjà un compte ────────────────────────────────────────────────

.footer {
  padding: 0 0 $spacing-xxxl;

  .already-account {
    margin: 0;
    padding: 0 $spacing-s;
    font-size: $font-size-sm;
    color: $text-secondary;
  }

  .login-link {
    padding: 0;
    border: none;
    background: none;
    color: $text-secondary;
    font-size: $font-size-sm;
    font-weight: $font-weight-semibold;
    text-decoration: underline;
    text-underline-offset: 0.25em;
    cursor: pointer;

    &:hover {
      color: $text-primary;
    }

    &:focus-visible {
      outline: 2px solid $brand-primary;
      outline-offset: 2px;
    }
  }
}
</style>
