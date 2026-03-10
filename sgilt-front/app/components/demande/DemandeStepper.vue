<template>
  <div
    class="stepper"
    role="progressbar"
    :aria-valuenow="etape"
    aria-valuemin="1"
    aria-valuemax="6"
  >
    <div v-for="n in 6" :key="n" class="stepper__item">
      <button
        class="stepper__dot"
        :class="{
          'stepper__dot--active': n === etape,
          'stepper__dot--done': n < etape,
          'stepper__dot--future': n > etape,
        }"
        :aria-label="`Étape ${n}`"
        :aria-current="n === etape ? 'step' : undefined"
        @click="n < etape ? $emit('go-to', n) : undefined"
      >
        <span v-if="n < etape" class="stepper__check">✓</span>
        <span v-else>{{ n }}</span>
      </button>
      <div v-if="n < 6" class="stepper__line" :class="{ 'stepper__line--done': n < etape }" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ etape: number }>()
defineEmits<{ (e: 'go-to', n: number): void }>()
</script>

<style scoped lang="scss">
.stepper {
  display: flex;
  align-items: center;
  padding: $spacing-m 0 $spacing-s;
  flex: 1;
  overflow: hidden;

  &__item {
    display: flex;
    align-items: center;
    flex: 1;

    &:last-child {
      flex: 0;
    }
  }

  &__dot {
    width: 2rem;
    height: 2rem;
    border-radius: 50%;
    border: 2px solid $divider-color;
    background: #fff;
    font-size: 0.75rem;
    font-weight: 700;
    color: $text-secondary;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: default;
    transition:
      background 200ms ease,
      border-color 200ms ease,
      color 200ms ease;
    flex-shrink: 0;
    padding: 0;

    &--active {
      background: $brand-accent;
      border-color: $brand-accent;
      color: #fff;
    }

    &--done {
      background: $brand-accent;
      border-color: $brand-accent;
      color: #fff;
      cursor: pointer;

      &:hover {
        opacity: 0.85;
      }
    }

    &--future {
      background: #fff;
      border-color: $divider-color;
      color: $text-secondary;
      opacity: 0.5;
    }
  }

  &__check {
    font-size: 0.7rem;
  }

  &__line {
    flex: 1;
    height: 2px;
    background: $divider-color;
    transition: background 200ms ease;

    &--done {
      background: $brand-accent;
    }
  }
}
</style>
