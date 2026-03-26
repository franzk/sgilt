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
    </div>
  </div>

  <!-- ── Variant 'sticky' ───────────────────────────────────────────────────── -->
  <div v-else-if="variant === 'sticky'" class="bca-sticky">
    <a :href="`tel:${phone}`" class="bca-sticky__btn call" aria-label="Appeler">
      <PhoneIcon class="bca-sticky__icon" />
      <span class="bca-sticky__label">Appeler</span>
    </a>
    <a :href="mailtoHref" class="bca-sticky__btn mail" aria-label="Envoyer un mail">
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
  layout: 'row' | 'column'
}>()

const phone = computed(() => props.clientInfo.phone.replace(/\s/g, ''))
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;
$action-icon-size: 5rem;

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

  @include pressable;
  &__icon {
    width: $action-icon-size;
    height: $action-icon-size;
    flex-shrink: 0;
  }

  &__label {
    font-family: 'Inter', sans-serif;
    font-size: 1rem;
    font-weight: 600;
    line-height: 1;
  }

  &--call {
    background: linear-gradient(180deg, #ffd24d 0%, #ffbf00 100%);
    color: #fff;
    border: 1.5px solid $brand-accent;
  }
  &--mail {
    background: #fff;
    color: #ffbf00;
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
  height: 3rem;
  background: #fff;
  // border-top: 1px solid $divider-color;
  border-top: 1px solid #e5dfd8;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-m;
  padding: $spacing-s $spacing-m;

  &__btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 18px;
    border-radius: $radius-md;
    border: 1.5px solid $divider-color;
    padding: 10px 14px;
    border-radius: 10px;
    text-decoration: none;
    color: $text-primary;
    cursor: pointer;
    @include pressable;

    &.call {
      background: linear-gradient(180deg, #ffd24d 0%, #ffbf00 100%);
      color: #fff;
      border: 1.5px solid $brand-accent;
    }

    &.mail {
      background: #fff;
      color: #ffbf00;
      border: 1.5px solid $divider-color;
    }

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
