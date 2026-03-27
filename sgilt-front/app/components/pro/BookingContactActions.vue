<template>
  <!-- ── Variant 'big' ──────────────────────────────────────────────────────── -->
  <div v-if="variant === 'big'" class="bca-big">
    <!-- Mobile : boutons horizontaux (masqués si desktopOnly) -->
    <div class="bca-big__mobile" :class="{ 'bca-big__mobile--hidden': desktopOnly }">
      <!-- Ligne 1 : Tel + Mail -->
      <div class="bca-btn-row">
        <a :href="`tel:${phone}`" class="bca-btn bca-btn--call">
          <PhoneIcon class="bca-btn__icon" />
          <span class="bca-btn__label">Appeler</span>
        </a>
        <a :href="mailtoHref" class="bca-btn bca-btn--mail">
          <MailSendIcon class="bca-btn__icon" />
          <span class="bca-btn__label">Mail</span>
        </a>
      </div>
      <!-- Ligne 2 : WhatsApp + SMS (mobile uniquement) -->
      <div v-if="isMobilePhone" class="bca-btn-row">
        <a :href="whatsappHref" class="bca-btn bca-btn--whatsapp" target="_blank" rel="noopener">
          <WhatsappIcon class="bca-btn__icon" />
          <span class="bca-btn__label">WhatsApp</span>
        </a>
        <a :href="smsHref" class="bca-btn bca-btn--sms">
          <ChatSmileIcon class="bca-btn__icon" />
          <span class="bca-btn__label">SMS</span>
        </a>
      </div>
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

        <!-- Carte mobile -->
        <ContactActionCard :copy-value="clientInfo.phone">
          <template #icon> <SmartphoneIcon v-if="isMobilePhone" /> <PhoneIcon v-else /> </template>
          <template #title>{{ isMobilePhone ? 'Mobile' : 'Tél' }}</template>
          <template #content>{{ clientInfo.phone }}</template>
          <template #cta>
            <a v-if="isMobilePhone" :href="whatsappHref" target="_blank" rel="noopener">
              <WhatsappIcon class="bca-cta-icon" />WhatsApp
            </a>
            <button v-else disabled class="bca-cta-disabled">
              <ForbidIcon class="bca-cta-icon" />WhatsApp
            </button>
          </template>
        </ContactActionCard>
      </div>
    </div>
  </div>

  <!-- ── Variant 'sticky' ───────────────────────────────────────────────────── -->
  <div
    v-else-if="variant === 'sticky'"
    class="bca-sticky"
    :class="{ 'bca-sticky--compact': isMobilePhone }"
  >
    <a :href="`tel:${phone}`" class="bca-sticky__btn bca-sticky__btn--call" aria-label="Appeler">
      <PhoneIcon class="bca-sticky__icon" />
      <span class="bca-sticky__label">Appeler</span>
    </a>
    <a :href="mailtoHref" class="bca-sticky__btn" aria-label="Envoyer un mail">
      <MailSendIcon class="bca-sticky__icon" />
      <span class="bca-sticky__label">Mail</span>
    </a>
    <a
      v-if="isMobilePhone"
      :href="whatsappHref"
      class="bca-sticky__btn bca-sticky__btn--whatsapp"
      target="_blank"
      rel="noopener"
      aria-label="WhatsApp"
    >
      <WhatsappIcon class="bca-sticky__icon" />
      <span class="bca-sticky__label">WhatsApp</span>
    </a>

    <a v-if="isMobilePhone" :href="smsHref" class="bca-sticky__btn" aria-label="SMS">
      <ChatSmileIcon class="bca-sticky__icon" />
      <span class="bca-sticky__label">SMS</span>
    </a>
  </div>
</template>

<script setup lang="ts">
import type { ClientContactInfo } from '~/types/event'
import { PhoneIcon, MailSendIcon, WhatsappIcon, ChatSmileIcon } from '@remixicons/vue/fill'
import { ForbidIcon, SmartphoneIcon } from '@remixicons/vue/line'
import ContactActionCard from '~/components/pro/ContactActionCard.vue'

const props = defineProps<{
  variant: 'big' | 'sticky'
  clientInfo: ClientContactInfo
  mailtoHref: string
  desktopOnly?: boolean
  layout: 'row' | 'column'
}>()

const phone = computed(() => props.clientInfo.phone.replace(/\s/g, ''))

const isMobilePhone = computed(() =>
  /^(06|07|\+336|\+337)/.test(props.clientInfo.phone.replace(/\s/g, '')),
)

const whatsappHref = computed(() => {
  const intl = phone.value.startsWith('0') ? '+33' + phone.value.slice(1) : phone.value
  return `https://wa.me/${intl.replace(/\+/, '')}`
})

const smsHref = computed(() => `sms:${phone.value}`)
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

// ── Variant big ────────────────────────────────────────────────────────────────
.bca-big__mobile {
  display: flex;
  flex-direction: column;
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

// ── Boutons mobiles horizontaux ────────────────────────────────────────────────
.bca-btn-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $spacing-s;
}

.bca-btn {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px $spacing-s;
  border-radius: $radius-md;
  text-decoration: none;
  cursor: pointer;
  @include pressable;

  &__icon {
    width: 20px;
    height: 20px;
    flex-shrink: 0;
  }

  &__label {
    font-family: 'Inter', sans-serif;
    font-size: 0.875rem;
    font-weight: 600;
    line-height: 1;
  }

  &--call {
    background: $brand-accent;
    color: #fff;
    border: 1.5px solid $brand-accent;
  }
  &--mail {
    background: #fff;
    color: $text-secondary;
    border: 1.5px solid $divider-color;
  }
  &--whatsapp {
    background: #fff;
    color: #25d366;
    border: 1.5px solid#25d366;
  }
  &--sms {
    background: #fff;
    color: $text-secondary;
    border: 1.5px solid $divider-color;
  }
}

// ── CTA carte mobile ───────────────────────────────────────────────────────────
.bca-cta-icon {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}

.bca-cta-disabled {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  justify-content: center;
  border: none;
  background: none;
  font-family: 'Inter', sans-serif;
  font-size: 0.8rem;
  font-weight: 600;
  color: $text-secondary;
  opacity: 0.45;
  cursor: not-allowed;
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

    &--call {
      background: $brand-accent;
      color: #fff;
      border-color: $brand-accent;
    }

    &--whatsapp {
      color: #25d366;
    }

    &:hover:not(&--call) {
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

  &--compact &__label {
    display: none;
  }
}
</style>
