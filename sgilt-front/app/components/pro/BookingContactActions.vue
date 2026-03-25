<template>
  <!-- ── Variant 'big' ──────────────────────────────────────────────────────── -->
  <div v-if="variant === 'big'" class="bca-big">
    <!-- Mobile : 2 carrés iconiques (masqués si desktopOnly) -->
    <div class="bca-big__mobile" :class="{ 'bca-big__mobile--hidden': desktopOnly }">
      <a :href="`tel:${phone}`" class="bca-square bca-square--call">
        <span class="bca-square__icon">📞</span>
        <span class="bca-square__label">Appeler</span>
      </a>
      <a :href="mailtoHref" class="bca-square bca-square--mail">
        <span class="bca-square__icon">✉️</span>
        <span class="bca-square__label">Mail</span>
      </a>
    </div>

    <!-- Desktop layout -->
    <div class="bca-big__desktop">
      <!-- Version cartes -->
      <template v-if="layout">
        <div class="bca-cards" :class="`bca-cards--${layout}`">
          <!-- Carte email -->
          <div class="bca-card">
            <div class="bca-card__header">
              <span class="bca-card__icon">✉️</span>
              <span class="bca-card__label">Email</span>
              <button
                class="bca-copy"
                type="button"
                :class="{ 'bca-copy--copied': emailCopied }"
                :aria-label="emailCopied ? 'Copié' : 'Copier l\'email'"
                @click="copyEmail"
              >{{ emailCopied ? '✓' : '⎘' }}</button>
            </div>
            <span class="bca-card__value">{{ clientInfo.email }}</span>
            <a :href="mailtoHref" class="bca-card__cta">Rédiger un mail</a>
          </div>

          <!-- Carte téléphone -->
          <div class="bca-card">
            <div class="bca-card__header">
              <span class="bca-card__icon">📞</span>
              <span class="bca-card__label">Téléphone</span>
              <button
                class="bca-copy"
                type="button"
                :class="{ 'bca-copy--copied': phoneCopied }"
                :aria-label="phoneCopied ? 'Copié' : 'Copier le numéro'"
                @click="copyPhone"
              >{{ phoneCopied ? '✓' : '⎘' }}</button>
            </div>
            <span class="bca-card__value">{{ clientInfo.phone }}</span>
            <a :href="`tel:${phone}`" class="bca-card__cta">Appeler</a>
          </div>
        </div>

        <!-- Actions en dessous des cartes -->
        <div v-if="showActions" class="bca-actions">
          <button
            class="bca-actions__confirm"
            type="button"
            :disabled="ctaLoading"
            @click="$emit('confirm')"
          >
            Contact effectué ✓
          </button>
          <button
            class="bca-actions__refuse"
            type="button"
            @click="$emit('refuse')"
          >
            Refuser la demande ✗
          </button>
        </div>
      </template>

      <!-- Version encarts (défaut) -->
      <template v-else>
        <div class="bca-encart">
          <a :href="mailtoHref" class="bca-encart__btn">
            ✉️ Envoyer un mail à {{ clientInfo.firstName }}
          </a>
          <div class="bca-encart__row">
            <span class="bca-encart__email">{{ clientInfo.email }}</span>
            <button
              class="bca-copy"
              type="button"
              :class="{ 'bca-copy--copied': emailCopied }"
              :aria-label="emailCopied ? 'Copié' : 'Copier l\'email'"
              @click="copyEmail"
            >{{ emailCopied ? '✓' : '⎘' }}</button>
          </div>
        </div>
        <div class="bca-encart">
          <span class="bca-encart__label">TÉLÉPHONE</span>
          <span class="bca-encart__phone">{{ clientInfo.phone }}</span>
        </div>
      </template>
    </div>
  </div>

  <!-- ── Variant 'sticky' ───────────────────────────────────────────────────── -->
  <div v-else-if="variant === 'sticky'" class="bca-sticky">
    <a :href="`tel:${phone}`" class="bca-sticky__btn" aria-label="Appeler">
      <span class="bca-sticky__icon">📞</span>
      <span class="bca-sticky__label">Appeler</span>
    </a>
    <a :href="mailtoHref" class="bca-sticky__btn" aria-label="Envoyer un mail">
      <span class="bca-sticky__icon">✉️</span>
      <span class="bca-sticky__label">Mail</span>
    </a>
  </div>
</template>

<script setup lang="ts">
import type { ClientContactInfo } from '~/types/event'

const props = defineProps<{
  variant: 'big' | 'sticky'
  clientInfo: ClientContactInfo
  mailtoHref: string
  desktopOnly?: boolean
  layout?: 'row' | 'column'
  showActions?: boolean
  ctaLoading?: boolean
}>()

defineEmits<{ confirm: []; refuse: [] }>()

const phone = computed(() => props.clientInfo.phone.replace(/\s/g, ''))

const emailCopied = ref(false)
const phoneCopied = ref(false)

async function copyEmail() {
  await navigator.clipboard.writeText(props.clientInfo.email)
  emailCopied.value = true
  setTimeout(() => (emailCopied.value = false), 2000)
}

async function copyPhone() {
  await navigator.clipboard.writeText(props.clientInfo.phone)
  phoneCopied.value = true
  setTimeout(() => (phoneCopied.value = false), 2000)
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

// ── Variant big ────────────────────────────────────────────────────────────────
.bca-big__mobile {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $spacing-s;

  @media (min-width: $desktop) { display: none; }

  &--hidden { display: none; }
}

.bca-big__desktop {
  display: none;

  @media (min-width: $desktop) {
    display: flex;
    flex-direction: column;
    gap: $spacing-s;
  }
}

// ── Carrés mobiles ─────────────────────────────────────────────────────────────
.bca-square {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  aspect-ratio: 1;
  border-radius: $radius-md;
  text-decoration: none;
  cursor: pointer;
  transition: opacity 150ms ease;

  &:active { opacity: 0.8; }
  &__icon { font-size: 1.75rem; line-height: 1; }

  &__label {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    font-weight: 600;
    line-height: 1;
  }

  &--call { background: $brand-accent; color: $brand-primary; border: none; }
  &--mail { background: #fff; color: $brand-primary; border: 1.5px solid $divider-color; }
}

// ── Cartes desktop ─────────────────────────────────────────────────────────────
.bca-cards {
  display: grid;
  gap: $spacing-s;

  &--row { grid-template-columns: 1fr 1fr; }
  &--column { grid-template-columns: 1fr; }
}

.bca-card {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
  background: #fff;
  border: 1px solid $divider-color;
  border-radius: $radius-md;
  padding: $spacing-s $spacing-m;
  box-shadow: 0 2px 10px rgba($brand-accent, 0.18);

  &__header {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  &__icon { font-size: 0.875rem; line-height: 1; flex-shrink: 0; }

  &__label {
    flex: 1;
    font-family: 'Inter', sans-serif;
    font-size: 0.7rem;
    font-weight: 600;
    letter-spacing: 0.07em;
    text-transform: uppercase;
    color: $text-secondary;
  }

  &__value {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    color: $text-primary;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__cta {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: auto;
    padding-top: $spacing-xs;
    padding-bottom: 2px;
    border: none;
    border-top: 1px solid $divider-color;
    background: none;
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    font-weight: 600;
    color: $brand-primary;
    text-decoration: none;
    cursor: pointer;
    transition: opacity 150ms ease;

    &:hover { opacity: 0.7; }
  }
}

// ── Actions sous les cartes ────────────────────────────────────────────────────
.bca-actions {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;

  @media (min-width: $desktop) {
    flex-direction: row;
  }

  &__confirm,
  &__refuse {
    display: flex;
    flex: 1;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 10px $spacing-m;
    border-radius: $radius-md;
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    font-weight: 700;
    letter-spacing: 0.04em;
    text-transform: uppercase;
    cursor: pointer;
    transition: opacity 150ms ease;

    @media (min-width: $desktop) {
      width: 0; // flex: 1 handles sizing
    }
  }

  &__confirm {
    border: none;
    background: #2e7d32;
    color: #fff;

    &:disabled { opacity: 0.5; cursor: default; }
    &:active:not(:disabled) { opacity: 0.8; }
  }

  &__refuse {
    border: 1.5px solid #c0392b;
    background: #fff;
    color: #c0392b;
    font-weight: 700;
    transition:
      background 120ms ease,
      border-color 120ms ease;

    @media (max-width: #{$desktop - 1px}) {
      border: none;
      background: none;
      font-size: 0.75rem;
      font-weight: 500;
      color: #c0392b;
      text-transform: none;
      letter-spacing: 0;
      padding: 4px 0;
    }

    &:hover {
      background: rgba(192, 57, 43, 0.04);
      border-color: rgba(192, 57, 43, 0.6);
    }
  }
}

// ── Encarts desktop (défaut) ───────────────────────────────────────────────────
.bca-encart {
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

    &:hover { opacity: 0.85; }
  }

  &__row {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    min-width: 0;
    border: 1px solid $divider-color;
    border-radius: $radius-sm;
    padding: 4px $spacing-xs;
  }

  &__email {
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

// ── Bouton copier ──────────────────────────────────────────────────────────────
.bca-copy {
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
  transition: background 120ms ease, color 120ms ease;

  &--copied {
    background: $brand-accent;
    color: $brand-primary;
    border-color: $brand-accent;
  }
}

// ── Variant sticky ─────────────────────────────────────────────────────────────
.bca-sticky {
  @media (min-width: $desktop) { display: none; }

  position: fixed;
  left: 0;
  right: 0;
  bottom: calc($bottom-nav-h + env(safe-area-inset-bottom, 0px));
  z-index: $z-header;
  height: 56px;
  background: #fff;
  border-top: 1px solid $divider-color;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-m;
  padding: 0 $spacing-m;

  &__btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 18px;
    border-radius: $radius-md;
    border: 1.5px solid $divider-color;
    background: #fff;
    text-decoration: none;
    color: $text-primary;
    cursor: pointer;
    transition: background 120ms ease;

    &:hover { background: $surface-soft; }
  }

  &__icon { font-size: 1.1rem; line-height: 1; }

  &__label {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    font-weight: 600;
  }
}
</style>
