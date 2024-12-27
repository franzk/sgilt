<template>
  <main class="home-view">
    <div class="search-banner-wrapper">
      <HomeSearchBanner />
    </div>

    <HomeCategoriesBanner />

    <HomePartnersSection />
  </main>
</template>

<script setup lang="ts">
import HomeSearchBanner from '@/components/pagecomponents/homepage/HomeSearchBanner.vue'
import HomePartnersSection from '@/components/pagecomponents/homepage/HomePartnersSection.vue'
import HomeCategoriesBanner from '@/components/pagecomponents/homepage/HomeCategoriesBanner.vue'
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
