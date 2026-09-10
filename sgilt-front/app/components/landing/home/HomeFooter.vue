<template>
  <footer class="home-footer">
    <div class="wrap">
      <div class="brand">
        <img src="/sgilt-logo.svg" alt="Sgilt" class="logo" />
        <p class="tagline">{{ $t('landing-page.footer.tagline') }}</p>
      </div>

      <nav class="links" aria-label="Pied de page">
        <NuxtLink to="/search">{{ $t('landing-page.footer.link-providers') }}</NuxtLink>
        <NuxtLink to="/m/pour-les-professionnels">{{ $t('landing-page.footer.link-pro') }}</NuxtLink>
        <button type="button" @click="handleLogin">{{ $t('landing-page.footer.link-login') }}</button>
      </nav>

      <!-- Masqués tant que les comptes Sgilt sur ces réseaux n'existent pas — repasser
           SOCIAL_LINKS_READY à true une fois les vraies URLs connues. -->
      <div v-if="SOCIAL_LINKS_READY" class="social" aria-label="Réseaux sociaux">
        <a href="#" :aria-label="$t('landing-page.footer.social-instagram')"
          ><InstagramIcon class="icon"
        /></a>
        <a href="#" :aria-label="$t('landing-page.footer.social-facebook')"
          ><FacebookIcon class="icon"
        /></a>
        <a href="#" :aria-label="$t('landing-page.footer.social-linkedin')"
          ><LinkedinIcon class="icon"
        /></a>
      </div>

      <nav class="legal" aria-label="Mentions légales">
        <NuxtLink to="/m/cgu">{{ $t('profile.menu.terms') }}</NuxtLink>
        <NuxtLink to="/m/confidentialite">{{ $t('profile.menu.privacy') }}</NuxtLink>
        <NuxtLink to="/m/mentions-legales">{{ $t('profile.menu.mentions-legales') }}</NuxtLink>
      </nav>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { InstagramIcon, FacebookIcon, LinkedinIcon } from '@remixicons/vue/line'
import { useKeycloak } from '~/composables/useKeycloak'

const SOCIAL_LINKS_READY = false

const { login } = useKeycloak()

function handleLogin() {
  login({ redirectUri: window.location.origin + '/auth/redirect' })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.home-footer {
  padding: $spacing-xl 0;
  background: $brand-primary;
  border-top: 1px solid rgba(255, 255, 255, 0.1);

  .wrap {
    max-width: $container-max-width;
    margin: 0 auto;
    padding: 0 $section-padding-x;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: $spacing-l;
  }

  .brand {
    .logo {
      height: 1.75rem;
      display: block;
      margin: 0 auto $spacing-xs;
      filter: brightness(0) invert(1);
    }

    .tagline {
      margin: 0;
      color: rgba(255, 255, 255, 0.6);
      font-size: $font-size-sm;
    }
  }

  .links,
  .legal {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: $spacing-m;

    a,
    button {
      padding: 0;
      border: none;
      background: none;
      color: $text-inverted;
      font: inherit;
      font-size: $font-size-sm;
      font-weight: $font-weight-medium;
      cursor: pointer;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
        text-underline-offset: 0.25em;
      }

      &:focus-visible {
        outline: 2px solid $brand-accent;
        outline-offset: 2px;
      }
    }
  }

  .legal a {
    color: rgba(255, 255, 255, 0.5);
    font-size: $font-size-xs;
  }

  .social {
    display: flex;
    gap: $spacing-m;

    a {
      display: flex;
      color: $text-inverted;

      &:hover {
        color: $brand-accent;
      }

      &:focus-visible {
        outline: 2px solid $brand-accent;
        outline-offset: 2px;
      }
    }

    .icon {
      width: 1.25rem;
      height: 1.25rem;
    }
  }
}
</style>
