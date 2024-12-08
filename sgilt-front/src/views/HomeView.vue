<template>
  <div class="home-view">
    <HomeSearchBox class="home-search-box" />
    <HomePartnerSection class="home-partner-section" />
  </div>
</template>

<script setup lang="ts">
import HomeSearchBox from '@/components/HomeSearchBox.vue'
import HomePartnerSection from '@/components/HomePartnerSection.vue'
</script>

<style lang="scss" scoped>
.home-view {
  //background: url('@/assets/images/home_bg.png');
  //box-shadow: inset 0 0 0 2000px rgba(200, 200, 200, 0.2);
  display: flex;
  flex-direction: column;

  .home-partner-section {
    background-color: white;
    //margin-top: 80px;
    padding-left: 1rem;
    padding-right: 1rem;
  }
}
</style>
