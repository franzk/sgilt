<template>
  <HomeSearchBox class="home-search-box" />
  <HomePartnerSection class="home-partner-section" />
</template>

<script setup lang="ts">
import HomeSearchBox from '@/components/HomeSearchBox.vue'
import HomePartnerSection from '@/components/HomePartnerSection.vue'
</script>

<style lang="scss" scoped>
.home-search-box {
  margin-top: 150px;
}
.home-partner-section {
  background-color: white;
  margin-top: 100px;
  padding-left: 1rem;
  padding-right: 1rem;
}
</style>
