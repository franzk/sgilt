<template>
  <div class="actions-page">
    <!-- ── Header ──────────────────────────────────────────────────────────── -->
    <div class="actions-page__header">
      <button class="back-btn" type="button" @click="navigateTo(`/pro/reservations/${demandeId}`)">
        {{ $t('pro.actions.back') }}
      </button>
      <div>
        <h1 class="actions-page__title">{{ $t('pro.actions.page-title') }}</h1>
        <p class="actions-page__subtitle">
          {{ $t('pro.actions.page-subtitle') }}
        </p>
      </div>
    </div>

    <!-- ── Liste des actions ────────────────────────────────────────────────── -->
    <div class="actions-list">
      <!-- Annuler la réservation -->
      <button
        class="action-item action-item--danger"
        type="button"
        :disabled="['annulee', 'refusee'].includes(currentStatut)"
        @click="cancelOpen = true"
      >
        <span class="action-item__icon">✕</span>
        <div class="action-item__body">
          <span class="action-item__label">{{ $t('pro.actions.cancel-reservation.label') }}</span>
          <span class="action-item__desc">
            {{
              ['annulee', 'refusee', 'realisee'].includes(currentStatut)
                ? $t('pro.actions.cancel-reservation.desc-inactive')
                : $t('pro.actions.cancel-reservation.desc-active')
            }}
          </span>
        </div>
      </button>
    </div>

    <!-- ── Modal confirmation annulation ───────────────────────────────────── -->
    <SgiltDialog
      v-if="cancelOpen"
      v-model:open="cancelOpen"
      :title="$t('pro.actions.cancel-reservation.dialog-title')"
      max-width="480px"
    >
      <div class="confirm-form">
        <p class="confirm-form__text">
          {{ $t('pro.actions.cancel-reservation.dialog-body') }}
        </p>
        <div class="confirm-form__actions">
          <button
            class="confirm-form__btn confirm-form__btn--cancel"
            type="button"
            @click="cancelOpen = false"
          >
            {{ $t('pro.actions.cancel-reservation.dialog-cancel') }}
          </button>
          <button
            class="confirm-form__btn confirm-form__btn--confirm"
            type="button"
            :disabled="loading"
            @click="confirmCancel"
          >
            {{ $t('pro.actions.cancel-reservation.dialog-confirm') }}
          </button>
        </div>
      </div>
    </SgiltDialog>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'pro' })

import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import { ProMockService } from '~/services/pro.mock'

const route = useRoute()
const demandeId = String(route.params.id)

const currentStatut = ref('nouvelle')
const loading = ref(false)
const cancelOpen = ref(false)

onMounted(async () => {
  const demande = await ProMockService.getDemandeById(demandeId)
  if (demande) currentStatut.value = demande.status
})

async function confirmCancel() {
  if (loading.value) return
  loading.value = true
  await ProMockService.updateStatut(demandeId, 'annulee')
  loading.value = false
  cancelOpen.value = false
  navigateTo('/pro/reservations')
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

$desktop: $breakpoint-desktop;

.actions-page {
  min-height: 100%;
  background-color: #f5f5f3;
  display: flex;
  flex-direction: column;

  &__header {
    background: #fff;
    border-bottom: 1px solid $divider-color;
    padding: $spacing-m;
    display: flex;
    align-items: center;
    gap: $spacing-m;

    @media (min-width: $desktop) {
      padding: $spacing-m 40px;
    }
  }

  &__title {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.4rem;
    font-weight: 600;
    color: $brand-primary;
    margin: 0;
    line-height: 1.2;
  }

  &__subtitle {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    color: $text-secondary;
    margin: 3px 0 0;
  }
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0;
  border: none;
  background: none;
  font-family: 'Inter', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  color: $text-secondary;
  cursor: pointer;
  flex-shrink: 0;
  transition: color 150ms ease;

  &:hover {
    color: $text-primary;
  }
}

.actions-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
  padding: $spacing-m;
  padding-bottom: calc($bottom-nav-h + env(safe-area-inset-bottom, 0px) + $spacing-m);

  @media (min-width: $desktop) {
    max-width: 600px;
    margin: 0 auto;
    width: 100%;
    padding: $spacing-l 0 $spacing-xl;
  }
}

.action-item {
  display: flex;
  align-items: center;
  gap: $spacing-m;
  padding: $spacing-m;
  background: #fff;
  border-radius: $radius-md;
  border: 0.5px solid rgba(47, 42, 37, 0.1);
  box-shadow: 0 1px 4px rgba(47, 42, 37, 0.07);
  text-align: left;
  cursor: pointer;
  transition: box-shadow 150ms ease;
  font-family: inherit;

  &:hover:not(:disabled) {
    box-shadow: 0 4px 16px rgba(47, 42, 37, 0.12);
  }

  &:disabled {
    opacity: 0.45;
    cursor: default;
    box-shadow: none;
  }

  &--danger {
    .action-item__icon {
      background: rgba(163, 45, 45, 0.1);
      color: #a32d2d;
    }

    .action-item__label {
      color: #a32d2d;
    }
  }

  &__icon {
    flex-shrink: 0;
    width: 40px;
    height: 40px;
    border-radius: $radius-md;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1rem;
  }

  &__body {
    display: flex;
    flex-direction: column;
    gap: 3px;
  }

  &__label {
    font-family: 'Inter', sans-serif;
    font-size: 0.9rem;
    font-weight: 600;
  }

  &__desc {
    font-family: 'Inter', sans-serif;
    font-size: 0.75rem;
    color: $text-secondary;
  }
}

// ── Confirmation modal ────────────────────────────────────────────────────────
.confirm-form {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: 32px;

  &__text {
    font-family: 'Inter', sans-serif;
    font-size: 0.9rem;
    color: $text-primary;
    line-height: 1.6;
    margin: 0;
  }

  &__actions {
    display: flex;
    gap: $spacing-s;
  }

  &__btn {
    flex: 1;
    height: 44px;
    border-radius: $radius-md;
    border: none;
    font-family: inherit;
    font-size: 0.875rem;
    font-weight: 600;
    cursor: pointer;
    transition: opacity 150ms ease;

    &:disabled {
      opacity: 0.5;
      cursor: default;
    }

    &--cancel {
      background: $surface-soft;
      color: $text-secondary;
      border: 1px solid $divider-color;
    }

    &--confirm {
      background: #a32d2d;
      color: #fff;

      &:hover:not(:disabled) {
        opacity: 0.85;
      }
    }
  }
}
</style>
