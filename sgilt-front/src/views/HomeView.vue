<template>
  <main class="home-view">
    <div class="search-banner-wrapper">
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

  .search-banner-wrapper {
    flex: 0 0 85%;
    justify-content: center;

    display: flex;
    flex-direction: column;
  }
}
</style>
