<template>
  <div class="partner-thumbnail">
    <img :src="imageUrl" alt="Partenaire 1" />
    <h3>{{ titre }}</h3>
    <p>{{ description }}</p>
    <p class="last-line">
      <span>{{ entryPrice }} €</span>
      <button>Voir le profil</button>
    </p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  imageUrl: string
  titre: string
  description: string
  entryPrice: number
}>()
</script>

<style lang="scss" scoped>
.partner-thumbnail {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  border: 1px solid black;
  img {
    width: 100%;
    height: auto;
  }
  h3,
  p {
    margin: 0;
    padding: 0.5rem;
  }
  .last-line {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 0.5rem;
    // button at the end
    button {
      background-color: #f1c40f;
      border: none;
      padding: 0.5rem 1rem;
      border-radius: 0.5rem;
      color: white;
      margin-left: auto;
    }
  }
}
</style>
