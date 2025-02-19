<template>
  <div class="hero" ref="hero">
    <div class="hero-overlay"></div>
    <h1>60 ANS DE Jean-Jacques</h1>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const hero = ref<HTMLElement | null>(null)

const handleScroll = () => {
  if (hero.value) {
    const scrollPosition = window.scrollY - 430
    hero.value.style.backgroundPositionY = `${scrollPosition * 0.5}px` // ✨ Ajuste le ratio pour plus d’effet
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss" scoped>
/* 🎉 Hero */
.hero {
  position: relative;
  width: 100%;
  height: 300px;
  background: url('@/assets/images/event-birthday.jpg') center/cover no-repeat;
  background-attachment: fixed; /* 🔥 Active l'effet parallaxe */
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  background-position-y: -250px;

  h1 {
    font-size: 4rem;
    font-weight: bold;
    font-family: 'Montserrat', sans-serif;
    text-transform: uppercase;
    color: $color-white;
    z-index: 2;
    text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  }
}

.hero-overlay {
  position: absolute;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(1px);
}
</style>
