<template>
  <section id="home-searchbox">
    <div class="title">Qu'est-ce qu'on FÊTE ?</div>
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
  gap: 1rem;

  background-color: rgba(20, 20, 0, 0.2);
  color: $color-secondary;
  padding: 2rem;
}

.title {
  font-size: 3rem;
  font-weight: bold;
  line-height: 3rem;
}

.search-fields {
  display: flex;
  flex-direction: row;
  gap: 2rem;
  .select-event {
    width: 14rem;
  }
}

.go {
  background-color: $color-accent;
  color: $color-secondary;
  border: none;
  border-radius: 0.5rem;
  padding: 0.5rem 1rem;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s;

  &:hover {
    background-color: $color-accent;
  }
}
</style>
