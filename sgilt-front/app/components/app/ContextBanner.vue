<template>
  <div class="banner">
    <div class="info">
      <span class="label">Ajouter à</span>
      <span class="event-name">{{ eventContext?.nom }}</span>
    </div>
    <button class="close" type="button" aria-label="Annuler" @click="abortOpen = true">✕</button>
  </div>

  <SgiltDialog v-model:open="abortOpen" title="Quitter la recherche ?" max-width="400px">
    <div class="abort-form">
      <p class="abort-text">
        Votre progression ne sera pas sauvegardée. Vous retournerez à l'événement.
      </p>
      <div class="abort-actions">
        <SgiltButton variant="secondary" @click="abortOpen = false">Continuer</SgiltButton>
        <SgiltButton @click="confirmAbort">Quitter</SgiltButton>
      </div>
    </div>
  </SgiltDialog>
</template>

<script setup lang="ts">
import SgiltDialog from '~/components/basics/dialogs/SgiltDialog.vue'
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

const { eventContext, abort } = useAddPrestataireContext()

const abortOpen = ref(false)

function confirmAbort() {
  abortOpen.value = false
  abort()
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.banner {
  position: sticky;
  top: 3.3rem; // $app-header-height
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-xs $spacing-m;
  background: $brand-primary;
  color: #fff;
  min-height: 44px;

  .info {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    min-width: 0;
  }

  .label {
    font-family: 'Inter', sans-serif;
    font-size: 0.72rem;
    font-weight: 500;
    opacity: 0.7;
    flex-shrink: 0;
  }

  .event-name {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1rem;
    font-weight: 600;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .close {
    flex-shrink: 0;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    border: 1px solid rgba(255, 255, 255, 0.3);
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
    font-size: 0.75rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background 150ms ease;
    margin-left: $spacing-s;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }
}

.abort-form {
  display: flex;
  flex-direction: column;
  gap: $spacing-l;
  padding: $spacing-m $spacing-l $spacing-l;

  .abort-text {
    font-family: 'Inter', sans-serif;
    font-size: 0.9rem;
    color: $text-secondary;
    line-height: 1.5;
    margin: 0;
  }

  .abort-actions {
    display: flex;
    gap: $spacing-s;
    justify-content: flex-end;
  }
}
</style>
