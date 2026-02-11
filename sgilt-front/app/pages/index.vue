<script setup lang="ts">
useSeoMeta({
  title: 'Sgilt — Prestataires en Alsace',

  description: 'Trouvez les meilleurs prestataires en Alsace, à votre date.',
})
</script>

<template>
  <div class="home">
    <div class="app-background">
      <div class="photo-layer" aria-hidden="true"></div>
    </div>

    <!-- tes composants viendront ici plus tard -->
  </div>
</template>

<style scoped lang="scss">
.home {
  position: relative;
  width: 100%;

  min-height: 100vh;
  min-height: 100dvh;
  height: auto;

  background-color: #fcfbf7;
  overflow: hidden;

  & > :not(.app-background) {
    position: relative;
    z-index: 2;
  }

  .app-background {
    position: absolute;
    z-index: 0;
    inset: 0;

    background-color: #fcfbf7;
    background-image: url('/textures/paper-grain.png');
    background-repeat: repeat;

    background-size: 700px;
    @media (min-width: 1024px) {
      background-size: 900px;
    }
  }

  .photo-layer {
    position: absolute;
    z-index: 1;

    top: 55%;
    @media (min-width: 1024px) {
      top: 45%;
    }
    left: 0;
    right: 0;
    bottom: 0;

    background-image: url('/images/hero-party.png');
    background-size: cover;
    background-repeat: no-repeat;
    background-position: 50% 100%;

    @media (min-width: 1024px) {
      background-position: 50% 75%;
    }

    -webkit-mask-image: linear-gradient(to bottom, transparent 0%, #000 18%, #000 100%);
    mask-image: linear-gradient(to bottom, transparent 0%, #000 18%, #000 100%);

    -webkit-mask-repeat: no-repeat;
    mask-repeat: no-repeat;

    -webkit-mask-size: 100% 100%;
    mask-size: 100% 100%;

    pointer-events: none;
  }
}
</style>
