<template>
  <section id="home-searchbox">
    <div class="search-box">
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
    </div>
  </section>
</template>

<script setup lang="ts"></script>

<style lang="scss" scoped>
#home-searchbox {
  background: url('@/assets/images/home_bg.png') no-repeat;
  box-shadow: inset 0 0 0 2000px rgba(200, 200, 200, 0.2);
  background-size: cover;
  padding-top: 180px;
  padding-bottom: 50px;
  display: flex;
  flex-direction: column;
}

.search-box {
  flex: 1;
  margin-left: auto;
  margin-right: auto;

  display: flex;
  flex-direction: row;
  gap: 3rem;
  align-items: center;

  border: 2px solid $color-dark;
  background-color: $color-light;
  padding: 0 4rem;
  border-radius: 0.2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.4);

  p {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>
