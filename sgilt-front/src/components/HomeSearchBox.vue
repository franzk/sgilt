<template>
  <section id="home-searchbox">
    <div class="title">
      <span class="title-thin">Qu'est-ce qu'on</span>
      <span class="title-bold"> FÊTE ?</span>
    </div>
    <div class="search-fields">
      <datepicker></datepicker>
      <SgiltSelect :options="options" class="select-event" />
      <button class="go">C'est Party !!</button>
    </div>
    <div class="valid-search"></div>
  </section>
</template>

<script setup lang="ts">
import datepicker from 'vuejs3-datepicker'
import SgiltSelect from '@/components/basics/SgiltSelect.vue'
const options = ['Votre évènement', 'Mariage', "Fête d'entreprise", "Fête d'anniversaire"]
</script>

<style lang="scss" scoped>
#home-searchbox {
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
  .select-event {
    width: 14rem;
  }
}

.go {
  background-color: $color-accent;
  color: $color-secondary;
  border: none;
  border-radius: $border-radius-s;
  padding: $spacing-s $spacing-m;
  font-size: $font-size-m;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s;

  &:hover {
    background-color: $color-accent;
  }
}
</style>
