<template>
  <div class="step-wrapper">
    <div :class="['step', 'step-' + index, { active: pawns && pawns.length }]">
      <p class="step-title">{{ label }}</p>
      <div class="step-box">
        <template v-if="pawns && pawns.length">
          <span v-for="pawn in pawns" :key="pawn" class="pawn">
            <img :src="pawn" />
          </span>
        </template>
        <div class="step-icon" v-else>
          <SgiltIcon icon="Sent" v-if="value === 'pending'" />
          <SgiltIcon icon="Eye" v-if="value === 'viewed'" />
          <SgiltIcon icon="CreditCard" v-if="value === 'approved'" />
          <SgiltIcon icon="Rocket" v-if="value === 'paied'" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'

defineProps<{
  value: string
  label: string
  index: number
  pawns?: string[]
}>()
</script>

<style scoped lang="scss">
$arrow-width: 3rem;
$step-spacing: 0.8rem;

@for $i from 0 through 3 {
  .step-#{$i} {
    background-color: lighten($color-accent, $i * 9%);
    transform: translateX(calc(($arrow-width - $step-spacing) * -1) * $i);
  }
}

.step-wrapper {
  filter: drop-shadow(0px 1px 5px rgba($color-primary, 0.5));
  margin-bottom: 4%;
}

.step-0 {
  clip-path: polygon(
    0% 0%,
    calc(100% - #{$arrow-width}) 0%,
    100% 50%,
    calc(100% - #{$arrow-width}) 100%,
    100% 100%,
    0% 100%
  );
}

.step-1,
.step-2,
.step-3 {
  clip-path: polygon(
    0% 0%,
    calc(100% - #{$arrow-width}) 0%,
    100% 50%,
    calc(100% - #{$arrow-width}) 100%,
    0% 100%,
    $arrow-width 50%
  );
}

.step-4 {
  clip-path: polygon(0% 0%, 100% 0%, 100% 100%, 0% 100%);
  background: $color-white;
  transform: translateX(calc(($arrow-width - $step-spacing) * -2.4));
}

.step-icon {
  position: absolute;
  bottom: 1rem;
  height: 4rem;
  width: 4rem;
  padding: 0.5rem;
  border: 3px solid rgba(0, 0, 0, 0.1);
  color: rgba(0, 0, 0, 0.1);
  opacity: 0.8;
  text-shadow:
    -2px -2px 3px rgba(255, 255, 255, 0.8),
    2px 2px 3px rgba(0, 0, 0, 0.15);
  filter: drop-shadow(1px 1px 2px rgba(0, 0, 0, 0.1));
  mix-blend-mode: multiply;
  transition: all 0.3s ease-in-out;
  box-shadow:
    inset 0 3px 6px rgba(0, 0, 0, 0.2),
    inset 0 -3px 6px rgba(255, 255, 255, 0.3),
    0px 3px 8px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(2px);

  border-radius: 50%;
}

.step:hover {
  .step-icon {
    box-shadow:
      inset 0 4px 8px rgba(0, 0, 0, 0.25),
      inset 0 -4px 8px rgba(255, 255, 255, 0.35),
      0px 4px 12px rgba(0, 0, 0, 0.15);
    transform: scale(1.02); // Mini zoom pour effet tactile
    transition: all 0.3s ease-in-out;
  }
}

.step {
  display: flex;
  flex-direction: column;

  height: 100%;

  position: relative;
  padding: $spacing-m;
  font-size: $font-size-base;
  font-weight: 600;
  border-radius: $border-radius-xs;
  transition: background 0.3s ease;

  // arrow space / arrow

  // no arrow space on first step
  /*&:first-child {
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
  }*/

  p {
    margin: 0;
  }

  .step-title {
    line-height: 1.4;
    padding: 0 1.2rem;
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
