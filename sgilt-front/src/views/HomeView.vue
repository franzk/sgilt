<template>
  <main class="home-view">
    <div class="search-section-wrapper">
      <HomeSearchSection />
    </div>

    <HomeCategoriesSection />

    <HomePartnersSection />
  </main>
</template>

<script setup lang="ts">
import HomeSearchSection from '@/components/HomeSearchSection.vue'
import HomePartnersSection from '@/components/HomePartnersSection.vue'
import HomeCategoriesSection from '@/components/HomeCategoriesSection.vue'
</script>

<style lang="scss" scoped>
.home-view {
  display: flex;
  flex-direction: column;

  background: url('@/assets/images/home_bg.jpg') no-repeat;
  background-size: cover;
  @include respond-to(tablet) {
    background-size: contain;
  }

  .search-section-wrapper {
    flex: 0 0 85%;
    @include respond-to(tablet) {
      flex: 0;
      aspect-ratio: 2;
    }

    @include respond-to(mobile) {
      aspect-ratio: initial;
    }

    justify-content: center;

    display: flex;
    flex-direction: column;
  }
}
</style>
