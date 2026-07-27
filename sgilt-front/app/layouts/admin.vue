<template>
  <AppHeader />
  <div class="admin-content">
    <div class="admin-nav-wrapper">
      <AdminNav />
    </div>
    <slot />
  </div>
</template>

<script setup lang="ts">
import AppHeader from '~/components/AppHeader.vue'
import AdminNav from '~/components/admin/AdminNav.vue'
</script>

<style lang="scss">
@use '@/assets/styles/base' as *;

.admin-content {
  padding-top: $app-header-height;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.admin-nav-wrapper {
  max-width: 720px;
  width: 100%;
  margin: 0 auto;
  padding: $spacing-l $spacing-l 0;
  box-sizing: border-box;
}
</style>
