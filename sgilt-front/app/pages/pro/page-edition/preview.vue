<template>
  <div class="tab-placeholder">
    <p>{{ $t('provider.edit.tabs.preview-placeholder') }}</p>
  </div>
</template>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.tab-placeholder {
  padding: $spacing-xl $spacing-m;
  text-align: center;
  color: $text-secondary;
}
</style>
