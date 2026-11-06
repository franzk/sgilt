<!--
  Page d'erreur de l'application (convention Nuxt) : remplace la page pour toute erreur non
  rattrapée — URL inconnue, slug d'événement inconnu (createError 404), erreur serveur. Dans le
  layout par défaut, avec l'en-tête, pour que le visiteur ne soit pas perdu. 404 : page
  introuvable ; tout autre statut : erreur générique. Un seul bouton, retour à l'accueil.
-->
<template>
  <NuxtLayout>
    <div class="error-page">
      <span class="badge" aria-hidden="true">
        <SignpostIcon v-if="notFound" />
        <ErrorWarningIcon v-else />
      </span>
      <h1 class="title">{{ $t(`errors.${messageKey}.title`) }}</h1>
      <p class="message">{{ $t(`errors.${messageKey}.message`) }}</p>
      <button type="button" class="primary-cta" @click="backHome">
        <HomeIcon class="icon" aria-hidden="true" />
        {{ $t('errors.back-home') }}
      </button>
    </div>
  </NuxtLayout>
</template>

<script setup lang="ts">
import type { NuxtError } from '#app'
import { ErrorWarningIcon, HomeIcon, SignpostIcon } from '@remixicons/vue/line'

const props = defineProps<{ error: NuxtError }>()

const { t } = useI18n()

const notFound = computed(() => props.error.statusCode === 404)
const messageKey = computed(() => (notFound.value ? 'not-found' : 'generic'))

useHead({ title: computed(() => t(`errors.${messageKey.value}.title`)) })

// clearError : sort de l'état d'erreur de Nuxt, sinon la page d'erreur resterait affichée.
function backHome(): Promise<void> {
  return clearError({ redirect: '/' })
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.error-page {
  width: 100%;
  max-width: 640px;
  margin: 0 auto;
  padding: $spacing-xxl $spacing-m;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-m;
  text-align: center;

  .badge {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 4rem;
    height: 4rem;
    border-radius: 50%;
    background: $surface-soft;
    color: $brand-primary;

    svg {
      width: 2rem;
      height: 2rem;
    }
  }

  .title {
    margin: 0;
    font-size: $font-size-xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
  }

  .message {
    margin: 0;
    font-size: $font-size-md;
    line-height: $line-height-relaxed;
    color: $text-secondary;
  }
}

// ─── CTA ─────────────────────────────────────────────────────────────────────
// Même bouton que les écrans de billetterie (confirmation).
.primary-cta {
  width: 100%;
  max-width: 20rem;
  margin-top: $spacing-s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5em;
  height: 3.25rem;
  border: none;
  border-radius: 9999px;
  background: $brand-accent;
  color: $brand-primary;
  box-shadow: 0 4px 10px rgba($brand-primary, 0.18);
  font-family: inherit;
  font-size: $font-size-md;
  font-weight: $font-weight-bold;
  cursor: pointer;
  transition:
    transform 160ms ease,
    box-shadow 160ms ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba($brand-primary, 0.22);
  }

  &:focus-visible {
    outline: 3px solid $brand-primary;
    outline-offset: 4px;
  }

  .icon {
    width: 1.1rem;
    height: 1.1rem;
  }
}
</style>
