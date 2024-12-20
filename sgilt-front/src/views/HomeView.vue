<template>
  <div class="home-view">
    <div class="search-banner">
      <HomeSearchBanner />
    </div>

    <HomeCategoriesBanner />

    <HomePartnersSection />
  </div>
</template>

<script setup lang="ts">
import HomeSearchBanner from '@/components/homepage/HomeSearchBanner.vue'
import HomePartnersSection from '@/components/homepage/HomePartnersSection.vue'
import HomeCategoriesBanner from '@/components/homepage/HomeCategoriesBanner.vue'
</script>

<style lang="scss" scoped>
.home-view {
  display: flex;
  flex-direction: column;

  background: url('@/assets/images/home_bg.jpg') no-repeat;
  background-size: cover;

  .search-banner {
    flex: 0 0 85%;
    justify-content: center;

    display: flex;
    flex-direction: column;
  }
}
</style>
