<template>
  <div class="step-wrapper">
    <div :class="['step', 'step-' + index, { active: pawns && pawns.length }]">
      <!-- step title -->
      <p class="step-title">{{ label }}</p>

      <!-- step content : either one/several pawns or svg placeholder -->
      <div class="step-box">
        <div class="step-content-wrapper">
          <template v-if="pawns && pawns.length">
            <!-- pawns : images of the partners -->
            <!-- Display 9 pawns if there are 9 or fewer partners, -->
            <!-- otherwise show the first 8 pawns followed by a "..." icon if there are more than 9 partners. -->
            <div
              v-for="(pawn, index) in (pawns ?? []).filter(
                (p, i) => i < ((pawns ?? []).length > 9 ? 8 : 9),
              )"
              :key="index"
              class="pawn"
            >
              <img :src="pawn" />
            </div>
            <!-- "..." icon -->
            <span v-if="pawns && pawns.length > 9" class="more-pawns"
              ><SgiltIcon icon="More"
            /></span>
          </template>

          <!-- icon : svg icon = placeholder when no pawn -->
          <div class="step-icon" v-else>
            <SgiltIcon icon="Sent" v-if="value === 'pending'" />
            <SgiltIcon icon="Eye" v-if="value === 'viewed'" />
            <SgiltIcon icon="CreditCard" v-if="value === 'approved'" />
            <SgiltIcon icon="Rocket" v-if="value === 'paid'" />
          </div>
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
$box-size: 7rem;

// color gradient
@for $i from 0 through 3 {
  .step-#{$i} {
    background-color: lighten($color-accent, $i * 9%);
    transform: translateX(calc(($arrow-width - $step-spacing) * -1) * $i);
  }
}

// shadow under the steps shape
.step-wrapper {
  filter: drop-shadow(0px 1px 5px rgba($color-primary, 0.5));
  margin-bottom: 1.75rem;
}

// first shape | >
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

// steps within > >
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

// step
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

  // title
  .step-title {
    margin: 0;
    line-height: 1.4;
    height: 2.8rem;
    padding: 0 1.2rem;
    transform: translateX(-0.3rem);
    text-align: center;
  }

  // step box : can contain either an svg icon or one or several pawns
  .step-box {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    transition: all 0.3s ease-in-out;

    .step-content-wrapper {
      display: grid;
      width: $box-size;
      height: $box-size;
      gap: 5px 10px;

      // grid layout depending on the number of pawns
      &:has(:nth-child(1)) {
        grid-template-columns: repeat(1, 1fr);
        grid-template-rows: repeat(1, 1fr);
      }
      &:has(:nth-child(2)) {
        grid-template-columns: repeat(2, 1fr);
        grid-template-rows: repeat(1, 1fr);
      }
      &:has(:nth-child(3)) {
        grid-template-columns: 1fr 1fr;
        grid-template-rows: 1fr 1fr;
        grid-template-areas: 'a a' 'b c';
      }
      &:has(:nth-child(4)) {
        grid-template-columns: repeat(2, 1fr);
        grid-template-rows: repeat(2, 1fr);
      }
      &:has(:nth-child(5)),
      &:has(:nth-child(6)) {
        grid-template-columns: repeat(3, 1fr);
        grid-template-rows: repeat(2, 1fr);
      }
    }

    // pawn : image of the partner
    .pawn {
      display: flex;
      align-items: center;
      justify-content: center;
      img {
        aspect-ratio: 1;
        width: 100%;
        border-radius: 50%;
        border: 3px solid $color-white;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
        object-fit: cover;
      }
    }

    // icon : svg icon = placeholder when no pawn
    .step-icon {
      border: 3px solid $color-white;
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
  }

  // icon and pawns hover effect
  &:hover {
    .step-icon,
    .pawn img {
      box-shadow:
        inset 0 4px 8px rgba(0, 0, 0, 0.25),
        inset 0 -4px 8px rgba(255, 255, 255, 0.35),
        0px 4px 12px rgba(0, 0, 0, 0.15);
      transform: scale(1.02);
      transition: all 0.3s ease-in-out;
    }
  }
}
</style>
