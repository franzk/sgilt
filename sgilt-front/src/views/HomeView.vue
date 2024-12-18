<template>
  <div class="home-view">
    <div class="search">
      <HomeSearchBox />
    </div>
    <HomePartnerSection />
  </div>
</template>

<script setup lang="ts">
import HomeSearchBox from '@/components/HomeSearchBox.vue'
import HomePartnerSection from '@/components/HomePartnerSection.vue'
</script>

<style lang="scss" scoped>
.home-view {
  background: url('@/assets/images/home_bg.jpg') no-repeat;
  background-size: cover;
  display: flex;
  flex-direction: column;
  .search {
    flex: 0 0 85%;
    justify-content: center;

    display: flex;
    flex-direction: column;
  }
}
</style>
