<template>
  <div class="contact-block">
    <!-- ── Mobile : 2 carrés iconiques ──────────────────────────────────────── -->
    <div class="contact-block__mobile">
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
    </div>

    <!-- ── Desktop : 3 blocs empilés ────────────────────────────────────────── -->
    <div class="contact-block__desktop">
      <!-- Bloc 1 — Email -->
      <div class="contact-encart">
        <a :href="mailtoHref" class="contact-encart__btn">
          ✉️ Envoyer un mail à {{ clientInfo.firstName }}
        </a>
        <div class="contact-encart__email-row">
          <span class="contact-encart__email-text">{{ clientInfo.email }}</span>
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

      <!-- Bloc 3 — Téléphone -->
      <div class="contact-encart">
        <span class="contact-encart__label">TÉLÉPHONE</span>
        <span class="contact-encart__phone">{{ clientInfo.phone }}</span>
      </div>

      <!-- Bloc 4 — Actions -->
      <div class="contact-desktop-actions">
        <button
          class="contact-block__action contact-block__action--confirm"
          type="button"
          :disabled="ctaLoading"
          @click="$emit('confirm')"
        >
          Contact effectué ✓
        </button>
        <button
          class="contact-block__action contact-block__action--refuse"
          type="button"
          @click="$emit('refuse')"
        >
          Refuser la demande
        </button>
      </div>
    </div>

    <!-- ── Actions (mobile uniquement) ───────────────────────────────────────── -->
    <div class="contact-block__mobile">
      <button
        class="contact-block__action contact-block__action--confirm"
        type="button"
        :disabled="ctaLoading"
        @click="$emit('confirm')"
      >
        Contact effectué ✓
      </button>

      <button
        class="contact-block__action contact-block__action--refuse"
        type="button"
        @click="$emit('refuse')"
      >
        Refuser la demande
      </button>
    </div>
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

const phoneCopied = ref(false)
const emailCopied = ref(false)

async function copyPhone() {
  await navigator.clipboard.writeText(props.clientInfo.phone)
  phoneCopied.value = true
  setTimeout(() => (phoneCopied.value = false), 2000)
}

async function copyEmail() {
  await navigator.clipboard.writeText(props.clientInfo.email)
  emailCopied.value = true
  setTimeout(() => (emailCopied.value = false), 2000)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.contact-block {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  background: #fff;
  border-radius: $radius-md;
  padding: $spacing-m;
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.07);

  @media (min-width: $desktop) {
    background: transparent;
    border-radius: 0;
    padding: 0;
    box-shadow: none;
    gap: $spacing-s;
  }
}

// ── Mobile ─────────────────────────────────────────────────────────────────────
.contact-block__mobile {
  display: contents;

  @media (min-width: $desktop) {
    display: none;
  }
}

.contact-block__row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $spacing-s;

  &--info .contact-block__info.tel {
    align-items: center;
    display: flex;
    justify-content: center;
  }
}

.contact-block__info {
  font-family: 'Inter', sans-serif;
  font-size: 0.72rem;
  color: $text-secondary;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.contact-block__email-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  overflow: hidden;
}

// ── Desktop ────────────────────────────────────────────────────────────────────
.contact-block__desktop {
  display: none;

  @media (min-width: $desktop) {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
  }
}

// Blocs 1 & 2 — Encarts Email / Téléphone
.contact-encart {
  background: #fff;
  border: 1px solid $divider-color;
  border-radius: $radius-md;
  padding: $spacing-s $spacing-m;
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  &__btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    padding: 9px $spacing-m;
    border-radius: $radius-sm;
    background: $brand-accent;
    color: #fff;
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    font-weight: 600;
    text-decoration: none;
    transition: opacity 150ms ease;

    &:hover {
      opacity: 0.85;
    }
  }

  &__email-row {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    min-width: 0;
    border: 1px solid $text-secondary;
    border-radius: 0.5rem;
    padding: 4px;
  }

  &__email-text {
    flex: 1;
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    color: $text-secondary;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__label {
    font-family: 'Inter', sans-serif;
    font-size: 0.65rem;
    font-weight: 600;
    letter-spacing: 0.09em;
    text-transform: uppercase;
    color: $text-secondary;
  }

  &__phone {
    font-family: 'Inter', sans-serif;
    font-size: 1.05rem;
    font-weight: 600;
    color: $text-primary;
    letter-spacing: 0.02em;
  }
}

// Bloc 4 — Actions (desktop)
.contact-desktop-actions {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
}

// ── Partagés ───────────────────────────────────────────────────────────────────
.contact-block__copy {
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

.contact-block__divider {
  border: none;
  border-top: 1px solid $divider-color;
  margin: 0;
}

.contact-block__action {
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

  @media (min-width: $desktop) {
    padding: 9px $spacing-m;
    font-size: 0.8rem;
  }

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
    background: none;
    border: none;
    color: #c0392b;
    font-weight: 400;
    font-size: 0.8rem;
    padding: 4px 0;
    justify-content: center;

    @media (min-width: $desktop) {
      padding: 0.5rem 0;
    }
  }
}

// ── Carrés iconiques (mobile uniquement) ───────────────────────────────────────
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
