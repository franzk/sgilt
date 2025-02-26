<template>
  <div class="game-board">
    <template v-for="(step, index) in steps" :key="index">
      <div :class="['step', 'step-' + index, { active: index === 0 }]">
        <p class="step-title">{{ step.label }}</p>
        <div class="step-box">
          <span v-if="index === 0" class="pawn">
            <img src="/images/jazz-band.jpg" />
          </span>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const steps = ref([
  { label: 'Demande envoyée' },
  { label: 'Demande reçue par le partenaire' },
  { label: 'Demande activée par le partenaire' },
  { label: 'En attente de paiement' },
  { label: 'DEAL 🎉' },
])
</script>

<style scoped lang="scss">
$arrow-width: 3rem;
$step-spacing: 0.8rem;
$step-height: 10rem;

@for $i from 0 through 3 {
  .step-#{$i} {
    background-color: lighten($color-accent, $i * 12%);
    transform: translateX(calc(($arrow-width - $step-spacing) * -1) * $i);
  }
}

.game-board {
  display: flex;
  flex-direction: row;
  padding: $spacing-l;
  position: relative;

  height: $step-height;
  padding-left: calc(($arrow-width - $step-spacing) * 3);

  background: linear-gradient(135deg, #f6f8fc 0%, #ebeff5 100%);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08);
}

.step {
  flex: 1;

  display: flex;
  flex-direction: column;
  position: relative;
  padding: $spacing-m;
  font-size: $font-size-base;
  font-weight: 600;
  border-radius: $border-radius-xs;
  transition: background 0.3s ease;

  // arrow space / arrow
  clip-path: polygon(
    0% 0%,
    calc(100% - #{$arrow-width}) 0%,
    100% 50%,
    calc(100% - #{$arrow-width}) 100%,
    0% 100%,
    $arrow-width 50%
  );

  // no arrow space on first step
  &:first-child {
    clip-path: polygon(
      0% 0%,
      calc(100% - #{$arrow-width}) 0%,
      100% 50%,
      calc(100% - #{$arrow-width}) 100%,
      100% 100%,
      0% 100%
    );
  }

  // no arrow shape on last step
  &:last-child {
    transform: translateX(calc(($arrow-width - $step-spacing) * -2.7));
    clip-path: polygon(0% 0%, 100% 0%, 100% 100%, 0% 100%);
    background: $color-white;
  }

  p {
    margin: 0;
  }

  .step-title {
    line-height: 1.4;
    padding: 0 1rem;
    transform: translateX(-0.3rem);
    text-align: center;
  }

  .step-box {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    transition: all 0.3s ease-in-out;

    .pawn {
      img {
        width: 5rem;
        aspect-ratio: 1;
        border-radius: 50%;
        border: 3px solid $color-white;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
        object-fit: cover;
      }
    }
  }
}
</style>
