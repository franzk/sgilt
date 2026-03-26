<template>
  <!-- ── Variant 'big' ──────────────────────────────────────────────────────── -->
  <div v-if="variant === 'big'" class="bca-big">
    <!-- Mobile : 2 carrés iconiques (masqués si desktopOnly) -->
    <div class="bca-big__mobile" :class="{ 'bca-big__mobile--hidden': desktopOnly }">
      <a :href="`tel:${phone}`" class="bca-square bca-square--call">
        <PhoneIcon class="bca-square__icon" />
        <span class="bca-square__label">Appeler</span>
      </a>
      <a :href="mailtoHref" class="bca-square bca-square--mail">
        <MailSendIcon class="bca-square__icon" />
        <span class="bca-square__label">Mail</span>
      </a>
    </div>

    <!-- Desktop layout -->
    <div class="bca-big__desktop">
      <!-- Version cartes -->
      <template v-if="layout">
        <div class="bca-cards" :class="`bca-cards--${layout}`">
          <!-- Carte email -->
          <ContactActionCard :copy-value="clientInfo.email">
            <template #icon><MailSendIcon /></template>
            <template #title>Email</template>
            <template #content>{{ clientInfo.email }}</template>
            <template #cta><a :href="mailtoHref">Rédiger un mail</a></template>
          </ContactActionCard>

          <!-- Carte téléphone -->
          <ContactActionCard :copy-value="clientInfo.phone">
            <template #icon><PhoneIcon /></template>
            <template #title>Téléphone</template>
            <template #content>{{ clientInfo.phone }}</template>
            <template #cta><a :href="`tel:${phone}`">Appeler</a></template>
          </ContactActionCard>
        </div>
      </template>

      <!-- Version encarts (défaut) -->
      <template v-else>
        <div class="bca-encart">
          <a :href="mailtoHref" class="bca-encart__btn">
            <MailSendIcon class="bca-encart__icon" />
            Envoyer un mail à {{ clientInfo.firstName }}
          </a>
          <div class="bca-encart__row">
            <span class="bca-encart__email">{{ clientInfo.email }}</span>
            <button
              class="bca-copy"
              type="button"
              :class="{ 'bca-copy--copied': emailCopied }"
              :aria-label="emailCopied ? 'Copié' : 'Copier l\'email'"
              @click="copyEmail"
            >
              <CheckIcon v-if="emailCopied" class="bca-copy__icon" />
              <FileCopyIcon v-else class="bca-copy__icon" />
            </button>
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
      <PhoneIcon class="bca-sticky__icon" />
      <span class="bca-sticky__label">Appeler</span>
    </a>
    <a :href="mailtoHref" class="bca-sticky__btn" aria-label="Envoyer un mail">
      <MailSendIcon class="bca-sticky__icon" />
      <span class="bca-sticky__label">Mail</span>
    </a>
  </div>
</template>

<script setup lang="ts">
import type { ClientContactInfo } from '~/types/event'
import { PhoneIcon, MailSendIcon } from '@remixicons/vue/fill'
import ContactActionCard from '~/components/pro/ContactActionCard.vue'

const props = defineProps<{
  variant: 'big' | 'sticky'
  clientInfo: ClientContactInfo
  mailtoHref: string
  desktopOnly?: boolean
  layout?: 'row' | 'column'
}>()

const phone = computed(() => props.clientInfo.phone.replace(/\s/g, ''))

</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;
$action-icon-size: 48px;

// ── Variant big ────────────────────────────────────────────────────────────────
.bca-big__mobile {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $spacing-s;

  @media (min-width: $desktop) {
    display: none;
  }

  &--hidden {
    display: none;
  }
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

  &:active {
    opacity: 0.8;
  }
  &__icon {
    width: $action-icon-size;
    height: $action-icon-size;
    flex-shrink: 0;
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

// ── Cartes desktop ─────────────────────────────────────────────────────────────
.bca-cards {
  display: grid;
  gap: $spacing-s;

  &--row {
    grid-template-columns: 1fr 1fr;
  }
  &--column {
    grid-template-columns: 1fr;
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

  &__icon {
    width: 14px;
    height: 14px;
    flex-shrink: 0;
  }

  &__btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
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

// ── Variant sticky ─────────────────────────────────────────────────────────────
.bca-sticky {
  @media (min-width: $desktop) {
    display: none;
  }

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

    &:hover {
      background: $surface-soft;
    }
  }

  &__icon {
    width: 20px;
    height: 20px;
    flex-shrink: 0;
  }

  &__label {
    font-family: 'Inter', sans-serif;
    font-size: 0.8rem;
    font-weight: 600;
  }
}
</style>
