<template>
  <div class="ia-tab">
    <div v-if="loadingState" class="tab-loading">{{ $t('provider.edit.ia.loading') }}</div>
    <FicheIaGenerationForm v-else-if="showForm || !result" @generated="showForm = false" />
    <FicheIaResult v-else @relaunch="showForm = true" />
  </div>
</template>

<script setup lang="ts">
import FicheIaGenerationForm from '~/components/prestataire/ficheia/FicheIaGenerationForm.vue'
import FicheIaResult from '~/components/prestataire/ficheia/FicheIaResult.vue'

const { result, loadingState, loadState } = useFicheIa()
const showForm = ref(false)

onMounted(() => loadState())
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.ia-tab {
  padding: $spacing-l $spacing-m;
  max-width: 640px;
  margin: 0 auto;
  width: 100%;

  @media (min-width: $breakpoint-desktop) {
    padding: $spacing-xl $spacing-m;
  }
}

.tab-loading {
  padding: $spacing-xl $spacing-m;
  text-align: center;
  color: $text-secondary;
}
</style>
