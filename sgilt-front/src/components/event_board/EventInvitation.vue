<template>
  <div class="invitation-card">
    <div class="clip"></div>
    <h1>Anniversaire de JJ</h1>
    <div class="event-details">
      <span class="status">🎉 Confirmé</span>
      <p>📍 Strasbourg</p>
      <p>📅 22 juin 2025</p>
    </div>
  </div>
</template>

<style scoped>
.invitation-card {
  position: relative;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 4px 4px 12px rgba(0, 0, 0, 0.2);
  font-family: 'Caveat', cursive;
  transform: rotate(-2deg);
}

/* Effet trombone */
.clip {
  width: 30px;
  height: 30px;
  background: url('/assets/trombone.svg') no-repeat center;
  position: absolute;
  top: -10px;
  left: 10px;
}
</style>
