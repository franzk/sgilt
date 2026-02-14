<template>
  <AppHeader />
  <section class="app-content">
    <NuxtPage />
  </section>
</template>

<style lang="scss">
@use '@/assets/styles/base' as *;

body {
  margin: 0;
  padding: 0;
}

:root {
  font-family: 'Poppins', sans-serif;
  font-weight: 400;
}

.app-content {
  padding-top: $app-header-height; // pour compenser la hauteur du header fixe
}
</style>
