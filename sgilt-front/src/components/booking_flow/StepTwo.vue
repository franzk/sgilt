<template>
  <!-- SSO Options -->
  <div class="sso-options">
    <button class="sso-btn google">
      {{ $t('first-reservation-modal.sso.google') }}
    </button>
    <button class="sso-btn facebook">
      {{ $t('first-reservation-modal.sso.facebook') }}
    </button>
    <button class="sso-btn apple">
      {{ $t('first-reservation-modal.sso.apple') }}
    </button>
  </div>

  <!-- Or Email -->
  <SgiltFormGroup
    :title="$t('booking-flow.email-label')"
    :error-message="reservationStore.emailError"
  >
    <input
      type="email"
      v-model="reservationStore.email"
      :placeholder="$t('booking-flow.email-placeholder')"
    />
  </SgiltFormGroup>

  <!-- Already have an account -->
  <p class="already-account">
    {{ $t('first-reservation-modal.already-account.title') }}
    <a href="/login" class="link">{{ $t('first-reservation-modal.already-account.link') }}</a>
  </p>

  <!-- Link to detailed page -->
  <p class="extra-info">
    <span>{{ $t('first-reservation-modal.extra-info.title') }}</span>
    <a href="/signup-info" class="link">{{ $t('first-reservation-modal.extra-info.link') }}</a>
  </p>
</template>

<script setup lang="ts">
import { useReservationStore } from '@/stores/reservation.store'
import SgiltFormGroup from '../basics/inputs/SgiltFormGroup.vue'

const reservationStore = useReservationStore()
</script>

<style scoped lang="scss">
/* SSO Buttons */
.sso-options {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  margin-bottom: $spacing-l;
  align-items: center;

  .sso-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0.8rem;
    border: none;
    border-radius: 5px;
    font-size: 1rem;
    cursor: pointer;
    transition: background-color 0.3s ease;
    width: 80%;

    /* Bouton Google */
    &.google {
      background-color: $color-white; /* Blanc pur */
      border: 1px solid $shadow-s; /* Bord léger */
      color: $color-primary; /* Texte sombre */
    }

    /* Bouton Facebook */
    &.facebook {
      background-color: #1877f2; /* Bleu Facebook */
      color: $color-white; /* Texte en blanc */
    }

    /* Bouton Apple */
    &.apple {
      background-color: $color-primary; /* Noir profond */
      color: $color-white; /* Texte en blanc */
    }

    img {
      margin-right: 0.5rem;
      height: 1rem;
    }

    &:hover {
      opacity: 0.9;
    }
  }
}

/* Divider */
.divider {
  font-size: $font-size-base;
  color: $color-subtext;
  margin: $spacing-m 0;
}

/* Email Form */
.email-form {
  display: flex;
  gap: $spacing-s;

  input {
    flex: 1;
    padding: $spacing-mm;
    font-size: 1rem;
    border: 1px solid $shadow-s;
    border-radius: $border-radius-xs;
    color: $color-primary;
    background-color: $color-white;

    &::placeholder {
      color: $color-subtext;
    }

    &:focus {
      outline: none;
      border: 1px solid $color-accent;
      box-shadow: 0 0 4px $color-accent;
    }
  }

  .email-btn {
    background-color: $color-accent;
    color: $color-white;
    border: none;
    padding: $spacing-mm $spacing-m;
    border-radius: $border-radius-xs;
    font-size: 1rem;
    cursor: pointer;
    transition: background-color 0.3s ease;

    &:hover {
      background-color: darken($color-accent, 10%);
    }
  }
}

.already-account {
  margin-top: $spacing-ml;
  font-size: $font-size-base;
  color: $color-subtext;
  text-align: center;

  .link {
    color: $color-accent;
    text-decoration: none;
    font-weight: bold;

    &:hover {
      text-decoration: underline;
    }
  }
}

/* Link to detailed page */
.extra-info {
  font-size: $font-size-base;
  color: $color-subtext;
  margin-bottom: 0;
  font-style: italic;
  justify-content: center;

  span {
    margin-right: $spacing-s;
  }

  .link {
    color: $color-accent;
    text-decoration: none;
    font-weight: bold;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
