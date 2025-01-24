<template>
  <header class="partner-header" v-if="partner">
    <div class="header-left">
      <img :src="partner.imageUrl" alt="Photo du partner" class="profile-picture" />
      <div>
        <h1>{{ partner.title }}</h1>
        <p class="slogan">{{ partner.description }}</p>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { usePartnerStore } from '@/stores/partner.store'

const partnerStore = usePartnerStore()
const partner = partnerStore.partner
</script>

<style scoped lang="scss">
$profile-picture-size: 8em;

.partner-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #eee;
  padding-bottom: $spacing-l;

  .header-left {
    display: flex;
    align-items: center;
    gap: $spacing-l;
  }

  .profile-picture {
    width: $profile-picture-size;
    height: $profile-picture-size;
    border-radius: 50%;
  }

  h1 {
    border-bottom: 2px solid $color-accent;
    margin-bottom: $spacing-s;
    padding-bottom: $spacing-s;
  }

  .slogan {
    font-style: italic;
    color: $color-subtext;
  }
}
</style>
