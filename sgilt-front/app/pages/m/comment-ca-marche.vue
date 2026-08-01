<template>
  <div class="how-it-works">
    <section class="hero">
      <div class="wrap">
        <p class="eyebrow">{{ $t('how-it-works.page.eyebrow') }}</p>
        <h1>{{ $t('how-it-works.page.title') }}</h1>
        <p class="lede">{{ $t('how-it-works.page.lede') }}</p>
      </div>
    </section>

    <div class="wrap content">
      <ol class="steps">
        <li class="step">
          <div class="marker">
            <span class="number">1</span>
            <span class="line" aria-hidden="true" />
          </div>
          <div class="text">
            <h2>{{ $t('how-it-works.page.step-1-title') }}</h2>
            <p>{{ $t('how-it-works.page.step-1-description') }}</p>
          </div>
        </li>
        <li class="step">
          <div class="marker">
            <span class="number">2</span>
            <span class="line" aria-hidden="true" />
          </div>
          <div class="text">
            <h2>{{ $t('how-it-works.page.step-2-title') }}</h2>
            <p>{{ $t('how-it-works.page.step-2-description') }}</p>
          </div>
        </li>
        <li class="step">
          <div class="marker">
            <span class="number">3</span>
          </div>
          <div class="text">
            <h2>{{ $t('how-it-works.page.step-3-title') }}</h2>
            <p>{{ $t('how-it-works.page.step-3-description') }}</p>
          </div>
        </li>
      </ol>

      <div class="cta-wrap">
        <NuxtLink to="/search" class="cta">
          {{ $t('how-it-works.page.cta') }}
        </NuxtLink>
      </div>
    </div>

    <div class="wrap footer">
      <p class="already-account">
        {{ $t('how-it-works.page.already-account') }}
        <button type="button" class="login-link" @click="handleLogin">
          {{ $t('how-it-works.page.login') }}
        </button>
      </p>
    </div>

    <section class="provider">
      <div class="wrap">
        <h2>{{ $t('how-it-works.page.provider-title') }}</h2>
        <p>{{ $t('how-it-works.page.provider-question') }}</p>
        <i18n-t keypath="how-it-works.page.provider-instructions" tag="p" scope="global">
          <template #email>
            <a href="mailto:contact@sgilt.alsace">contact@sgilt.alsace</a>
          </template>
        </i18n-t>
        <p class="callback">{{ $t('how-it-works.page.provider-callback') }}</p>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { useKeycloak } from '~/composables/useKeycloak'

useHead({ title: 'Comment ça marche' })

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

// ── Schéma en 3 étapes ─────────────────────────────────────────────────────────

.content {
  padding: $spacing-xxxl 0;
  max-width: 640px;
}

.steps {
  list-style: none;
  margin: 0 0 $spacing-xxl;
  padding: 0;
}

.step {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: $spacing-l;

  .marker {
    display: flex;
    flex-direction: column;
    align-items: center;

    .number {
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      width: 2.75rem;
      height: 2.75rem;
      border-radius: 50%;
      background: rgba($brand-accent, 0.15);
      color: $brand-primary;
      font-family: 'Cormorant Garamond', serif;
      font-size: $font-size-xl;
      font-weight: $font-weight-semibold;
    }

    .line {
      flex: 1;
      width: 2px;
      min-height: $spacing-l;
      margin: $spacing-xs 0;
      background: $divider-color;
    }
  }

  .text {
    padding-bottom: $spacing-xl;

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

  &:last-child .text {
    padding-bottom: 0;
  }
}

.cta-wrap {
  display: flex;
  justify-content: center;
}

.cta {
  display: inline-flex;
  align-items: center;
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

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 28px rgba($brand-accent, 0.28);
  }

  &:focus-visible {
    outline: 3px solid $brand-primary;
    outline-offset: 4px;
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

// ── Vous êtes prestataire ? ──────────────────────────────────────────────────────

.provider {
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
</style>
