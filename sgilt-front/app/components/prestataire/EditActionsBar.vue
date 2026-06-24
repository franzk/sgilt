<template>
  <div class="edit-actions">
    <SgiltButton class="btn-full" @click="onSave">
      {{ saved ? $t('provider.edit.saved') : $t('provider.edit.save') }}
    </SgiltButton>
    <SgiltButton variant="secondary" class="btn-full" @click="onSubmit">
      {{ $t('provider.edit.submit') }}
    </SgiltButton>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'

const saved = ref(false)

function onSave() {
  saved.value = true
}

function onSubmit() {
  // stub — branché au point 4
}
</script>

<style scoped lang="scss">
.edit-actions {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  width: 100%;
}

.btn-full {
  width: 100%;
}
</style>
