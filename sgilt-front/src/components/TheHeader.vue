<template>
  <header>
    <h1>
      <router-link :to="{ name: 'home' }"><img src="@/assets/images/sgilt.png" /></router-link>
      <!-- TODO resize image -->
    </h1>
    <p class="tagline">
      {{ $t('app-header.tagline.decouvrez') }}
      {{ $t('app-header.tagline.reservez') }}
      <span class="bold">{{ $t('app-header.tagline.celebrez') }}</span>
    </p>
  </header>
</template>

<script setup lang="ts"></script>

<style lang="scss" scoped>
header {
  flex: 0;

  display: flex;
  flex-direction: row;

  position: sticky;
  top: 0;
  left: 0;
  z-index: 2;

  background-color: $color-secondary;
  box-shadow: $box-shadow;

  gap: $spacing-l;
  align-content: center;
  flex-wrap: wrap;

  padding: $spacing-m;

  h1 {
    margin: 0 0 0 $spacing-xs;
    img {
      height: 3rem;
      margin-bottom: $spacing-xs;
    }

    a {
      text-decoration: none;
      color: $color-primary;
      display: flex;
    }
  }

  .tagline {
    padding-top: 0.8em;
    margin: 0;
    @include respond-to(mobile) {
      display: none;
    }
    -webkit-align-self: center;
    align-self: center;

    .bold {
      font-weight: bold;
    }
  }
}
</style>
