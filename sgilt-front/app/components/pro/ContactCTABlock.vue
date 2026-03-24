<template>
  <div class="contact-block">
    <!-- Ligne 1 : carrés de contact (50/50) -->
    <div class="contact-block__row contact-block__row--btns">
      <a :href="`tel:${phone}`" class="contact-cta contact-cta--call">
        <span class="contact-cta__icon">📞</span>
        <span class="contact-cta__label">Appeler</span>
      </a>
      <a :href="mailtoHref" class="contact-cta contact-cta--mail">
        <span class="contact-cta__icon">✉️</span>
        <span class="contact-cta__label">Mail</span>
      </a>
    </div>

    <!-- Ligne 2 : infos de contact (50/50) -->
    <div class="contact-block__row contact-block__row--info">
      <span class="contact-block__info tel">{{ clientInfo.phone }}</span>
      <div class="contact-block__email-cell">
        <span class="contact-block__info">{{ clientInfo.email }}</span>
        <button
          class="contact-block__copy"
          type="button"
          :class="{ 'contact-block__copy--copied': emailCopied }"
          :aria-label="emailCopied ? 'Copié' : 'Copier l\'email'"
          @click="copyEmail"
        >
          {{ emailCopied ? '✓' : '⎘' }}
        </button>
      </div>
    </div>

    <hr class="contact-block__divider" />

    <!-- Ligne 3 : Contact effectué -->
    <button
      class="contact-block__action contact-block__action--confirm"
      type="button"
      :disabled="ctaLoading"
      @click="$emit('confirm')"
    >
      Contact effectué ✓
    </button>

    <!-- Ligne 4 : Refuser -->
    <button
      class="contact-block__action contact-block__action--refuse"
      type="button"
      @click="$emit('refuse')"
    >
      Refuser la demande
    </button>
  </div>
</template>

<script setup lang="ts">
import type { ClientContactInfo } from '~/types/event'

const props = defineProps<{
  clientInfo: ClientContactInfo
  mailtoHref: string
  ctaLoading?: boolean
}>()

defineEmits<{ confirm: []; refuse: [] }>()

const phone = computed(() => props.clientInfo.phone.replace(/\s/g, ''))

const emailCopied = ref(false)

async function copyEmail() {
  await navigator.clipboard.writeText(props.clientInfo.email)
  emailCopied.value = true
  setTimeout(() => (emailCopied.value = false), 2000)
}
</script>

<style scoped lang="scss">
.contact-block {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  background: #fff;
  border-radius: $radius-md;
  padding: $spacing-m;
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.07);

  &__row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: $spacing-s;

    &--info {
      gap: $spacing-s;
      .contact-block__info.tel {
        align-items: center;
        display: flex;
        justify-content: center;
      }
    }
  }

  &__info {
    flex: 50%;

    font-family: 'Inter', sans-serif;
    font-size: 0.72rem;
    color: $text-secondary;
    text-align: center;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__email-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 5px;
    overflow: hidden;
  }

  &__copy {
    flex-shrink: 0;
    width: 22px;
    height: 22px;
    border-radius: $radius-sm;
    border: 1px solid $divider-color;
    background: none;
    font-size: 0.75rem;
    color: $text-secondary;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition:
      background 120ms ease,
      color 120ms ease;

    &--copied {
      background: $brand-accent;
      color: $brand-primary;
      border-color: $brand-accent;
    }
  }

  &__divider {
    border: none;
    border-top: 1px solid $divider-color;
    margin: 0;
  }

  &__action {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    padding: 12px $spacing-m;
    border-radius: $radius-md;
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    font-weight: 600;
    cursor: pointer;
    transition: opacity 150ms ease;

    &:disabled {
      opacity: 0.5;
      cursor: default;
    }

    &:active:not(:disabled) {
      opacity: 0.8;
    }

    &--confirm {
      background: #2e7d32;
      color: #fff;
      border: none;
    }

    &--refuse {
      background: #fff;
      color: #c0392b;
      border: 1px solid rgba(192, 57, 43, 0.35);
      font-weight: 500;
    }
  }
}

.contact-cta {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  aspect-ratio: 1;
  box-sizing: border-box;
  border-radius: $radius-md;
  text-decoration: none;
  cursor: pointer;
  transition: opacity 150ms ease;

  &:active {
    opacity: 0.8;
  }

  &__icon {
    font-size: 1.75rem;
    line-height: 1;
  }

  &__label {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    font-weight: 600;
    line-height: 1;
  }

  &--call {
    background: $brand-accent;
    color: $brand-primary;
    border: none;
  }

  &--mail {
    background: #fff;
    color: $brand-primary;
    border: 1.5px solid $divider-color;
  }
}
</style>
