<template>
  <div class="event-dashboard">
    <!-- 🛸 Noyau central - Status Hub -->
    <div class="status-hub">
      <h2>🚀 État de l’événement</h2>
      <div class="status-core">
        <div class="tag status validated">✅ Validé</div>
        <div class="tag payment">💳 Paiement en attente</div>
        <div class="tag location">📍 Strasbourg</div>
        <div class="tag date">📅 22 juin 2025 à 19:00</div>
        <div class="tag guests">👥 20 invités</div>
      </div>
    </div>

    <!-- 🔵 Réservations -->
    <div class="dashboard-module reservations">
      <h3>🎸 Réservations</h3>
      <div class="reservations-list">
        <p>📋 Liste des réservations en cours...</p>
      </div>
    </div>

    <!-- 🟠 Journal des événements -->
    <div class="dashboard-module event-journal">
      <h3>📜 Journal</h3>
      <div class="journal-list">
        <p>📅 Historique des interactions...</p>
      </div>
    </div>

    <!-- 🟢 Messagerie & Fichiers -->
    <div class="dashboard-actions">
      <button class="quick-action">💬 Messages</button>
      <button class="quick-action">📂 Fichiers</button>
    </div>
  </div>
</template>

<script setup lang="ts">
// import { ref } from 'vue'

// const status = ref('validated') // Exemple de statut dynamique
</script>

<style scoped>
/* 🛸 Mise en page du tableau de bord */
.event-dashboard {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  height: 100vh;
  overflow: hidden;
}

/* 🛸 Noyau central - Status Hub */
.status-hub {
  position: relative;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 10%, rgba(0, 0, 0, 0.7) 90%);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
  box-shadow: 0px 0px 15px rgba(255, 255, 255, 0.2);
  transition: 0.3s ease-in-out;
}

/* 💡 Effet lumineux interactif */
.status-hub:hover {
  box-shadow: 0px 0px 25px rgba(255, 255, 255, 0.6);
}

/* 🎛️ Tags centrés autour du noyau */
.status-core {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.tag {
  background: #4caf50;
  color: white;
  padding: 8px 14px;
  border-radius: 20px;
  font-size: 0.85rem;
  text-transform: uppercase;
  white-space: nowrap;
}

/* 🔵 Modules flottants autour du noyau */
.dashboard-module {
  position: absolute;
  width: 250px;
  height: 250px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  box-shadow: 0px 0px 10px rgba(255, 255, 255, 0.2);
  transition: 0.3s;
}

/* 🌀 Positionnement des modules */
.reservations {
  left: 10%;
  top: 20%;
}
.event-journal {
  right: 10%;
  top: 20%;
}
.dashboard-actions {
  bottom: 10%;
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 15px;
}

/* 🟢 Animations survol */
.dashboard-module:hover {
  box-shadow: 0px 0px 20px rgba(255, 255, 255, 0.5);
  transform: scale(1.1);
}

/* 📬 Actions rapides */
.dashboard-actions {
  grid-column: span 3;
  display: flex;
  justify-content: center;
  gap: 15px;
}

.quick-action {
  padding: 10px 16px;
  border: none;
  background: #222;
  color: white;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s;
}

.quick-action:hover {
  background: #444;
}
</style>
