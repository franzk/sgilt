<template>
  <div class="banner">
    <span class="label">{{ flowLabel }}</span>
    <button class="close" type="button" :aria-label="$t('common.cancel')" @click="abortOpen = true">✕</button>
  </div>

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
import SgiltConfirmDialog from '~/components/basics/dialogs/SgiltConfirmDialog.vue'

const { flowLabel, abort: abortFlow } = useFlow()
const { reset: resetDemande } = useDemande()
const abortOpen = ref(false)

function abort() {
  resetDemande()
  abortFlow()
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.banner {
  position: fixed;
  top: $app-header-height;
  left: 0;
  right: 0;
  z-index: $z-header - 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-xs $spacing-m;
  background: $brand-primary;
  color: #fff;
  min-height: 44px;

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
}
</style>
