<template>
  <header>
    <h1>S'gilt !!</h1>
    <p>Recherchez, réservez, célébrez !!</p>
  </header>
</template>

<script setup lang="ts"></script>

<style lang="scss" scoped>
header {
  position: sticky;
  top: 0;
  left: 0;

  background-color: $color-light;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);

  display: flex;
  flex: 1;
  gap: 2rem;
  align-content: center;
  flex-wrap: wrap;

  padding-left: 1rem;

  p {
    padding: 0;
    margin: 0;
    align-content: inherit;
  }
}
</style>
