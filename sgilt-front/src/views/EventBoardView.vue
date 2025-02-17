<template>
  <div class="event-board">
    <!-- Hero Section -->
    <div class="hero" ref="hero">
      <div class="hero-overlay"></div>
      <h1>60 ANS DE Jean-Jacques</h1>
    </div>

    <!-- Cockpit HUD -->
    <div class="event-dashboard">
      <div class="hud-outline">
        <!--h2>🚀 État de l’événement</!--h2-->
        <div class="hud-tags">
          <span class="hud-tag success">✔️ Validé</span>
          <span class="hud-tag warning">⚠️ Paiement en attente</span>
          <span class="hud-tag location">📍 Strasbourg</span>
          <span class="hud-tag date">📅 22 juin 2025 à 19:00</span>
          <span class="hud-tag guests">👥 20 invités</span>
        </div>
        <div class="hud-actions">
          <button class="hud-btn">💬 Messages</button>
          <button class="hud-btn">📂 Fichiers</button>
        </div>
      </div>
    </div>

    <!-- Board Sections -->
    <div class="event-content">
      <section class="reservations">
        <h3>🎸 Réservations</h3>
        <p>📋 Liste des réservations en cours...</p>
      </section>
      <section class="journal">
        <h3>📜 Journal</h3>
        <p>📅 Historique des interactions...</p>
      </section>
    </div>
  </div>
  <div style="height: 2000px"></div>
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
/* 🌟 General Layout */
.event-board {
  text-align: center;
  background: $color-secondary;
}

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

/* 🛸 Cockpit HUD */
.event-dashboard {
  position: relative;
  margin: -80px auto 30px;
  width: 30%;
  aspect-ratio: 1;
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hud-outline {
  position: relative;
  // width: 100%;
  height: 100%;
  aspect-ratio: 1;
  border-radius: 50%;
  border: 2px solid $color-accent;
  box-shadow: 0 0 15px $color-accent;
  // background: rgba(255, 255, 255, 0.9);
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 🔥 Tags */
.hud-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.hud-tag {
  padding: 8px 15px;
  border-radius: 15px;
  font-size: 0.9rem;
  font-weight: bold;
  text-transform: uppercase;
  display: flex;
  align-items: center;
  background: $color-white;
  border: 2px solid transparent;
  box-shadow: 0 0 5px $shadow-m;
}

/* 🎨 Tag Colors */
.success {
  border-color: $color-available;
  color: $color-available;
}

.warning {
  border-color: $color-error;
  color: $color-error;
}

.location {
  border-color: $color-primary;
  color: $color-primary;
}

.date {
  border-color: $color-divider;
  color: $color-divider;
}

.guests {
  border-color: $color-subtext;
  color: $color-subtext;
}

/* ✨ HUD Actions */
.hud-actions {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}

.hud-btn {
  padding: 8px 12px;
  font-size: 0.9rem;
  border: none;
  border-radius: 8px;
  background: $color-primary;
  color: $color-white;
  cursor: pointer;
  transition: 0.3s ease-in-out;

  &:hover {
    background: $color-accent;
    box-shadow: 0 0 10px $color-accent;
  }
}

/* 📊 Board Sections */
.event-content {
  display: flex;
  justify-content: space-around;
  padding: 30px;
}

.reservations,
.journal {
  background: $color-white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 5px 10px $shadow-m;
  width: 45%;
  text-align: left;
}

h3 {
  font-size: 1.5rem;
  font-weight: bold;
}
</style>
