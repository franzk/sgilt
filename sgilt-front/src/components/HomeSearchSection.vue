<template>
  <section id="search-banner">
    <div class="title">
      <span class="title-thin">{{ $t('home.search-banner.title-part-1') }}</span>
      <span class="title-bold">{{ $t('home.search-banner.title-part-2') }}</span>
    </div>
    <div class="search-fields">
      <SgiltDatePicker />
      <SgiltSelect :options="options" class="select-event" />
      <SgiltSimpleButton @click="search">{{ $t('home.search-banner.button') }}</SgiltSimpleButton>
    </div>
  </section>
</template>

<script setup lang="ts">
import SgiltDatePicker from '@/components/basics/inputs/SgiltDatePicker.vue'
import SgiltSelect from '@/components/basics/inputs/SgiltSelect.vue'
import SgiltSimpleButton from '@/components/basics/buttons/SgiltSimpleButton.vue'

import router from '@/router'

const options = ['Votre évènement', 'Mariage', "Fête d'entreprise", "Fête d'anniversaire"]

const search = () => {
  router.push({ path: '/search' })
}
</script>

<style lang="scss">
#search-banner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  gap: $spacing-m;
  padding: $spacing-l;

  background-color: $shadow;
  color: $color-secondary;
}

.title {
  font-size: $font-size-xxl;
  line-height: $line-height-xl;
  .title-thin {
    font-weight: 300;
  }
  .title-bold {
    font-weight: bold;
  }
}

.search-fields {
  display: flex;
  flex-direction: row;
  gap: $spacing-xl;
  width: 100%;
  justify-content: center;

  .select-event {
    line-height: 45px;
    width: 17rem;
  }
}
</style>
