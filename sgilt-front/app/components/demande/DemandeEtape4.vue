<template>
  <div class="etape">
    <h2 class="question">{{ $t('tunnel.etape4.question') }}</h2>
    <p class="subtitle">{{ $t('tunnel.etape4.subtitle-before') }}</p>

    <textarea
      v-model="state.description"
      class="description-textarea"
      :placeholder="placeholderText"
      rows="6"
    />

    <p class="subtitle">{{ $t('tunnel.etape4.subtitle-after') }}</p>

    <div v-if="isDesktop" class="actions">
      <SgiltButton @click="next">{{ $t('tunnel.footer.continue') }}</SgiltButton>
      <SgiltButton variant="tertiary" @click="next">{{ $t('tunnel.footer.skip') }}</SgiltButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import { useDemande } from '~/composables/useDemande'

const { state, next } = useDemande()
const { isDesktop } = useDevice()

const { t } = useI18n()
const placeholderText = computed(() => t('tunnel.etape4.placeholder'))
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

  .actions {
    display: flex;
    align-items: center;
    gap: $spacing-m;
    margin-top: $spacing-s;
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
