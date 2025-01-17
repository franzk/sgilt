<template>
  <div class="reservation-modal" v-if="showModal" @click.self="closeModal">
    <div class="modal-content">
      <!-- Title -->
      <h2>{{ $t('first-reservation-modal.title') }}</h2>
      <p class="subtitle">{{ $t('first-reservation-modal.subtitle') }}</p>

      <!-- SSO Options -->
      <div class="sso-options">
        <button class="sso-btn google" @click="connectWithSSO('google')">
          {{ $t('first-reservation-modal.sso.google') }}
        </button>
        <button class="sso-btn facebook" @click="connectWithSSO('facebook')">
          {{ $t('first-reservation-modal.sso.facebook') }}
        </button>
        <button class="sso-btn apple" @click="connectWithSSO('apple')">
          {{ $t('first-reservation-modal.sso.apple') }}
        </button>
      </div>

      <!-- Or Email -->
      <p class="divider">{{ $t('first-reservation-modal.email.title') }}</p>
      <div class="email-form">
        <input
          type="email"
          v-model="email"
          :placeholder="$t('first-reservation-modal.email.placeholder')"
          @keydown="emailError = ''"
        />
        <!--button class="email-btn" @click="connectWithEmail"-->
        <SgiltButton @click="connectWithEmail">
          {{ $t('first-reservation-modal.email.button') }}
        </SgiltButton>
        <!--button-->
      </div>
      <p class="error-msg">{{ emailError }}&nbsp;</p>

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

      <!-- Close Button -->
      <button class="close-btn" @click="closeModal">&times;</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import SgiltButton from '@/components/basics/buttons/SgiltButton.vue'

// Props or parent control
defineProps<{
  showModal: boolean
}>()

// Data
const email = ref<string>('')
const emailError = ref<string | null>(null)

// Emit events
const emit = defineEmits(['close', 'connectSSO', 'connectEmail'])

// Methods
const closeModal = () => emit('close')

const connectWithSSO = (provider: string) => emit('connectSSO', provider)

const validateEmail = () => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  emailError.value = emailRegex.test(email.value.trim())
    ? null
    : 'Veuillez entrer une adresse email valide.'
}

const connectWithEmail = () => {
  validateEmail()
  if (!emailError.value) {
    emit('connectEmail', email.value.trim())
  }
}
</script>

<style scoped lang="scss">
/* Modal Container */
.reservation-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: $modal-overlay;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: $z-modal;
}

/* Modal Content */
.modal-content {
  background-color: $color-secondary;
  padding: $spacing-l;
  border-radius: $border-radius-s;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 10px 30px $shadow-l;
  position: relative;
  text-align: center;
}

/* Title */
h2 {
  color: $color-primary;
  margin-bottom: $spacing-m;
}

/* Subtitle */
.subtitle {
  font-size: 1rem;
  color: $color-subtext;
  margin-bottom: $spacing-l;
}

/* SSO Buttons */
.sso-options {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  margin-bottom: $spacing-l;

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

/* Close Button */
.close-btn {
  position: absolute;
  top: $spacing-s;
  right: $spacing-s;
  font-size: 2rem;
  color: $color-subtext;
  background: none;
  border: none;
  cursor: pointer;
  transition: transform 0.2s ease;

  &:hover {
    transform: scale(1.2);
    color: $color-primary;
  }
}

/* Error message */
.error-msg {
  color: $color-error;
  font-size: $font-size-base;
  margin-top: $spacing-xs;
  text-align: left;
  margin-left: $spacing-m;
}
</style>
