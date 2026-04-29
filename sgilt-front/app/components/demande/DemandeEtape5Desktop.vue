<template>
  <div class="etape">
    <h2 class="question">Votre événement en pratique</h2>
    <DemandeEtape5Fields :state="state" :ville-error="villeError" />
    <SgiltButton class="button" @click="handleContinue">
      {{ $t('tunnel.footer.continue') }}
    </SgiltButton>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import DemandeEtape5Fields from '~/components/demande/DemandeEtape5Fields.vue'
import { useDemande } from '~/composables/useDemande'

const { t } = useI18n()
const { state, next } = useDemande()

const villeError = ref<string | null>(null)

watch(() => state.ville, () => { villeError.value = null })

function handleContinue() {
  if (!state.ville.trim()) {
    villeError.value = t('tunnel.etape5.error-ville')
    return
  }
  next()
}
</script>

<style scoped lang="scss">
.etape {
  .question {
    font-family: 'Cormorant Garamond', serif;
    font-size: 1.5rem;
    font-weight: 500;
    color: $text-primary;
    margin: 0 0 $spacing-l;
    line-height: 1.3;
  }

  :deep(.fields) {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: $spacing-m;
  }

  .button {
    margin-top: $spacing-l;
  }
}
</style>
