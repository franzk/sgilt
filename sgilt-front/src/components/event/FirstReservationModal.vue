<template>
  <div class="reservation-modal" v-if="showModal" @click.self="closeModal">
    <div class="modal-content">
      <!-- Title -->
      <h2>Réservez votre prestataire en quelques secondes</h2>
      <p class="subtitle">Choisissez une méthode pour continuer :</p>

      <!-- SSO Options -->
      <div class="sso-options">
        <button class="sso-btn google" @click="connectWithSSO('google')">
          <img src="" alt="Google" /> Continuer avec Google
        </button>
        <button class="sso-btn facebook" @click="connectWithSSO('facebook')">
          <img src="" alt="Facebook" /> Continuer avec Facebook
        </button>
        <button class="sso-btn apple" @click="connectWithSSO('apple')">
          <img src="" alt="Apple" /> Continuer avec Apple
        </button>
      </div>

      <!-- Or Email -->
      <p class="divider">Ou utilisez votre email :</p>
      <div class="email-form">
        <input type="email" v-model="email" placeholder="Votre email" @keydown="emailError = ''" />
        <button class="email-btn" @click="connectWithEmail">Continuer</button>
      </div>
      <p class="error-msg">{{ emailError }}&nbsp;</p>

      <!-- Link to detailed page -->
      <p class="extra-info">
        <span>Découvrez tous les avantages d’un compte Sgilt.</span>
        <a href="/signup-info" class="link">En savoir plus</a>
      </p>

      <!-- Close Button -->
      <button class="close-btn" @click="closeModal">&times;</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// Props or parent control
const props = defineProps<{
  showModal: boolean
}>()

// Data
const email = ref<string>('')
const emailError = ref<string | null>(null)

// Emit events
const emit = defineEmits(['close', 'connectSSO', 'connectEmail'])

// Methods
const closeModal = () => {
  emit('close')
}

const connectWithSSO = (provider: string) => {
  emit('connectSSO', provider)
}

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
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* Modal Content */
.modal-content {
  background-color: $color-secondary; /* Background clair */
  padding: 2rem;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 10px 30px $shadow-l; /* Ombre douce */
  position: relative;
  text-align: center;
}

/* Title */
h2 {
  font-size: 1.8rem;
  color: $color-primary; /* Titre en noir profond */
  margin-bottom: 1rem;
}

/* Subtitle */
.subtitle {
  font-size: 1rem;
  color: $color-subtext;
  margin-bottom: 1.5rem;
}

/* SSO Buttons */
.sso-options {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;

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
  font-size: 0.9rem;
  color: $color-subtext;
  margin: 1rem 0;
}

/* Email Form */
.email-form {
  display: flex;
  gap: 0.5rem;

  input {
    flex: 1;
    padding: 0.8rem;
    font-size: 1rem;
    border: 1px solid $shadow-s; /* Bord léger */
    border-radius: 5px;
    color: $color-primary; /* Texte principal */
    background-color: $color-white; /* Fond clair */

    &::placeholder {
      color: $color-subtext;
    }

    &:focus {
      outline: none;
      border: 1px solid $color-accent; /* Accentuation */
      box-shadow: 0 0 4px $color-accent; /* Lueur subtile */
    }
  }

  .email-btn {
    background-color: $color-accent; /* Bouton accent jaune */
    color: $color-white; /* Texte blanc */
    border: none;
    padding: 0.8rem 1rem;
    border-radius: 5px;
    font-size: 1rem;
    cursor: pointer;
    transition: background-color 0.3s ease;

    &:hover {
      background-color: darken($color-accent, 10%); /* Jaune légèrement assombri */
    }
  }
}

/* Link to detailed page */
.extra-info {
  font-size: 0.9rem;
  color: $color-subtext;
  margin-top: 2em;
  margin-bottom: 0;

  span {
    font-style: italic;
    margin-right: $spacing-s;
  }

  .link {
    color: $color-accent; /* Jaune accent */
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
  top: 1rem;
  right: 1rem;
  font-size: 2rem;
  color: $color-subtext;
  background: none;
  border: none;
  cursor: pointer;
  transition: transform 0.2s ease;

  &:hover {
    transform: scale(1.2);
    color: $color-primary; /* Noir profond au hover */
  }
}

/* Message d'erreur */
.error-msg {
  color: #ff4d4f; /* Rouge clair pour alerter */
  font-size: 0.875rem; /* Texte discret */
  margin-top: 0.5rem;
  text-align: left;
  margin-left: 1em;
}
</style>
