<template>
  <AppBanner tone="dark">
    <span class="label">{{ flowLabel }}</span>
    <button class="close" type="button" :aria-label="$t('common.cancel')" @click="abortOpen = true">
      ✕
    </button>
  </AppBanner>

  <SgiltConfirmDialog
    v-model:open="abortOpen"
    :title="$t('common.context-banner.quit-title')"
    :message="$t('common.context-banner.quit-message')"
    :confirm-label="$t('common.context-banner.quit')"
    :cancel-label="$t('common.context-banner.continue')"
    max-width="400px"
    @confirm="abort"
  />
</template>

<script setup lang="ts">
import AppBanner from '~/components/app/AppBanner.vue'
import SgiltConfirmDialog from '~/components/basics/dialogs/SgiltConfirmDialog.vue'

const { currentFlow, flowLabel, abort: abortFlow } = useFlow()
const { reset: resetDemande } = useDemande()
const { reset: resetLocalEvent } = useLocalEvent()
const abortOpen = ref(false)

function abort() {
  resetDemande()
  // Abandonner la création d'un événement abandonne aussi le brouillon local ;
  // abandonner l'ajout d'un prestataire à un événement existant n'y touche pas.
  if (currentFlow.value === 'new-event') resetLocalEvent()
  abortFlow()
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.label {
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
</style>
