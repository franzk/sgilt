<template>
  <div class="unauthorized">
    <h1 class="title">{{ $t('unauthorized.title') }}</h1>
    <p class="message">{{ $t('unauthorized.message') }}</p>
    <button class="btn" @click="logout">
      {{ $t('unauthorized.logout') }}
    </button>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'default' })

const { logout } = useKeycloak()
</script>

<style lang="scss" scoped>
.unauthorized {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: $spacing-m;
  padding: $spacing-xl $spacing-m;
  text-align: center;

  .title {
    font-size: $font-size-xl;
    font-weight: $font-weight-medium;
    color: $text-primary;
  }

  .message {
    font-size: $font-size-md;
    color: $text-secondary;
    max-width: 480px;
  }

  .btn {
    font-size: $font-size-md;
    font-weight: $font-weight-medium;
    font-family: inherit;
    padding: $spacing-s $spacing-l;
    background-color: $color-primary;
    color: $text-inverted;
    border: none;
    border-radius: $border-radius-xxl;
    cursor: pointer;
  }
}
</style>
