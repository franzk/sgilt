<template>
  <div class="etape">
    <h2 class="question">Racontez-nous votre événement</h2>
    <p class="subtitle">Décrivez-le comme vous l'imaginez…</p>

    <textarea
      v-model="state.description"
      class="description-textarea"
      :placeholder="placeholderText"
      rows="6"
    />

    <p class="subtitle">
      Plus vous nous en dites, mieux les prestataires pourront comprendre votre projet et y répondre
      de manière pertinente !
    </p>

    <p class="optional">Optionnel — vous pouvez passer cette étape.</p>

    <p class="next">
      <SgiltButton class="next-btn" @click="next"> Continuer →</SgiltButton>
    </p>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { useDemande } from '~/composables/useDemande'

const { state, next } = useDemande()

const placeholderText = 'Parlez-nous de votre événement, de vos envies, de ce que vous imaginez...'
</script>

<style scoped lang="scss">
.etape {
  .question {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.5rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0 0 $spacing-xs;
    line-height: 1.3;
  }

  .subtitle {
    font-size: 0.9rem;
    color: $text-secondary;
    margin: 0 0 $spacing-l;
  }

  .optional {
    font-size: 0.8rem;
    color: $text-secondary;
    opacity: 0.6;
    margin: $spacing-s 0 0;
  }

  .next {
    height: 3rem;
    width: 100%;
    display: flex;
    button {
      width: 100%;
    }
  }
}

.description-textarea {
  width: 100%;
  padding: $spacing-m;
  border: 1.5px solid $divider-color;
  border-radius: $radius-md;
  font-size: 0.95rem;
  font-family: inherit;
  color: $text-primary;
  background: #fff;
  resize: vertical;
  outline: none;
  transition: border-color 180ms ease;
  box-sizing: border-box;
  line-height: 1.6;

  &:focus {
    border-color: $brand-accent;
  }

  &::placeholder {
    color: $text-secondary;
    opacity: 0.5;
  }
}
</style>
