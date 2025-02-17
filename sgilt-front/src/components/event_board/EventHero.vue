<template>
  <div class="hero">
    <div class="hero-overlay"></div>
    <div class="hero-content">
      <div class="event-title">
        <h1>{{ eventTitle }}</h1>
      </div>
      <div class="bottom-section">
        <div class="event-info">
          <IconPlace />
          <span>{{ eventLocation }}</span>
          <IconClock />
          <span>{{ eventDate }}</span>
        </div>
      </div>
      <!--div class="event-status">
        <span v-if="isValidated" class="validated">✅ Événement validé</span>
        <span v-else class="pending">⚠️ En attente de validation</span>
      </!--div-->
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import IconPlace from '../icons/IconPlace.vue'
import IconClock from '../icons/IconClock.vue'

const eventTitle = ref('60 ans de JJ')
const eventDate = ref('22 juin 2025 à 19:00')
const eventLocation = ref('Strasbourg')
// const isValidated = ref(false) // Simule le statut
</script>

<style scoped lang="scss">
/* 🏞️ HERO SECTION */
.hero {
  display: flex;
  flex-direction: column;

  position: relative;
  width: 100%;
  height: 320px;
  background: url('@/assets/images/event-birthday.jpg') center/cover no-repeat;

  align-items: center;
  justify-content: center;
  text-align: center;
  overflow: hidden;
}

/* 🌚 Overlay */
.hero-overlay {
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.5) 0%,
    rgba(0, 0, 0, 0.3) 60%,
    rgba(0, 0, 0, 0) 100%
  );
  backdrop-filter: blur(1px); /* Applique le flou */
}

/* ✨ Hero content */
.hero-content {
  display: flex;
  flex-direction: column;
  gap: $spacing-m;
  position: relative;
  color: white;
  z-index: $z-first-floor;
  width: 100%;
  height: 100%;
  justify-content: center;
}

/* 🏆 Titre principal */
.event-title {
  font-family: 'Montserrat', sans-serif;
  text-transform: uppercase;
  h1 {
    margin: 0;
    letter-spacing: 1.2px;
    text-shadow: 2px 2px 10px rgba(0, 0, 0, 0.9);
    font-weight: 700;
    font-size: 5rem; // à variabiliser
    line-height: 5rem;
  }
}

.bottom-section {
  position: absolute;
  width: 100%;
  bottom: 0;
  height: 3rem;

  display: flex;
  justify-content: center;
  padding: $spacing-s;
}

/* 📅 Infos de l’événement */
.event-info {
  align-items: center;
  font-size: $font-size-h3;
  padding: $spacing-s $spacing-m;
  display: flex;
  gap: $spacing-m;
  background: rgba(0, 0, 0, 0.6);
  border-radius: $border-radius-l;
  svg {
    width: 2rem;
    height: 2rem;
  }
}

/* ✅ Statut de l’événement avec effet badge */
.event-status {
  // margin-top: 1rem;
  font-weight: 500;
  // font-size: 1.2rem;
  flex: 1;
  padding: $spacing-m 0;
  // display: inline-block;
  // padding: 6px 14px;
  // border-radius: 6px;
  span {
    padding: $spacing-s;
    border-radius: $border-radius-l;
  }
}

/* 🎨 Couleurs des statuts */
.validated {
  color: white;
  background-color: #4caf50; /* Vert validé */
}

.pending {
  color: white;
  background-color: #ff9800; /* Orange en attente */
}

.warning {
  color: white;
  background-color: #e53935; /* Rouge pour paiements urgents */
}
</style>
