<template>
  <section id="home-searchbox">
    <p class="event-date">
      <span>Date de votre évènement</span>
      <input type="date" />
    </p>
    <p class="event-type">
      <span>Qu'est-ce qu'on fête ?</span>
      <select>
        <option>Mariage</option>
        <option>Fête d'entreprise</option>
        <option>Fête d'anniversaire</option>
      </select>
    </p>
    <p class="valid-search">
      <button>Rechercher</button>
    </p>
  </section>
</template>

<script setup lang="ts"></script>

<style lang="scss" scoped>
#home-searchbox {
  flex: 1;
  margin-left: auto;
  margin-right: auto;

  align-items: center;
  display: flex;
  flex-direction: row;
  gap: 3rem;

  border: 1px solid black;
  padding: 0 4rem;
  border-radius: 0.5rem;

  p {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>
