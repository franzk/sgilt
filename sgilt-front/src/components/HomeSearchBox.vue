<template>
  <section id="home-searchbox">
    <div class="search-box">
      <div class="title">Dites-nous en plus sur votre événement ...</div>
      <div class="search-fields">
        <p class="event-date">
          <datepicker></datepicker>
        </p>
        <p class="event-type">
          <SgiltSelect :options="options" />
        </p>
        <p class="valid-search">
          <button>Rechercher</button>
        </p>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import datepicker from 'vuejs3-datepicker'
import SgiltSelect from '@/components/basics/SgiltSelect.vue'
const options = ["Qu'est-ce qu'on fête ?", 'Mariage', "Fête d'entreprise", "Fête d'anniversaire"]
</script>

<style lang="scss" scoped>
#home-searchbox {
  background: url('@/assets/images/home_bg.png') no-repeat;
  box-shadow: inset 0 0 0 2000px rgba(200, 200, 200, 0.2);
  background-size: cover;
  padding-top: 180px;
  padding-bottom: 50px;
  display: flex;
  flex-direction: column;

  .search-box {
    flex: 1;
    margin-left: auto;
    margin-right: auto;

    display: flex;
    flex-direction: column;
    gap: 1rem;

    border: 1px solid $color-dark;
    background-color: $color-light;
    padding: 1rem 1rem 0 1rem;
    border-radius: 0.1rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.4);

    .title {
      font-size: 2rem;
      font-weight: bold;
      color: $color-dark;
      text-align: center;
      // margin-bottom: 2rem;
    }

    .search-fields {
      display: flex;
      flex-direction: row;
      gap: 3rem;
      align-items: center;

      p {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
      }

      .event-type {
        flex: 1;
      }
    }
  }
}
</style>
