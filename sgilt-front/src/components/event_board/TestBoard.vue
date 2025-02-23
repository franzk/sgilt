<template>
  <div class="game-board">
    <template v-for="(step, index) in steps" :key="index">
      <div class="step-container">
        <div :class="['step', step.status]">
          <div class="step-icon" v-if="step.image && false">
            <img :src="step.image" alt="Pion du prestataire" />
          </div>
          <span class="step-text">{{ step.label }}</span>
        </div>
        <div v-if="index < steps.length - 1" class="arrow"></div>
      </div>
    </template>
  </div>
</template>

<script setup>
const steps = [
  { label: 'Demande envoyée', status: 'completed', image: '/images/jazz-band.jpg' },
  { label: 'Demande reçue par le partenaire', status: 'completed' },
  { label: 'Demande activée par le partenaire', status: 'active' },
  { label: 'En attente de paiement', status: 'pending' },
  { label: 'DEAL 🎉', status: 'final-step' },
]
</script>

<style scoped lang="scss">
.game-board {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  padding: 2rem;

  .step {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    width: 200px;
    height: 80px;
    background: #fff;
    border-radius: 0;
    font-weight: bold;
    font-size: 1rem;
    color: #333;
    border: 2px solid #ddd;
    transition: 0.3s ease-in-out;

    // Ajout de la flèche intégrée dans la case
    &::after {
      content: '';
      position: absolute;
      right: -20px;
      top: 50%;
      transform: translateY(-50%);
      width: 0;
      height: 0;
      border-left: 20px solid #fff; // La couleur doit matcher avec le bg de la case suivante
      border-top: 40px solid transparent;
      border-bottom: 40px solid transparent;
      z-index: 1;
    }

    &:last-child::after {
      content: none; // Pas de flèche sur la dernière case
    }

    // CASE ACTIVE (où le joueur est)
    &.active {
      background: #ffd700;
      border-color: #ffa500;
      box-shadow: inset -3px -3px 8px rgba(255, 165, 0, 0.5);
    }

    // CASES VALIDÉES
    &.done {
      background: #4caf50;
      color: #fff;
      border-color: #388e3c;
    }

    // DERNIÈRE CASE (DEAL)
    &.deal {
      background: #e53935;
      color: #fff;
      border-color: #b71c1c;
    }
  }
}
</style>
