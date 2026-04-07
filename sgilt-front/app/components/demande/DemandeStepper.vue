<template>
  <div
    class="stepper"
    :class="{ vertical: vertical }"
    role="progressbar"
    :aria-valuenow="etape"
    aria-valuemin="1"
    aria-valuemax="6"
  >
    <div v-for="n in 6" :key="n" class="item">
      <div class="dot-col">
        <button
          class="dot"
          :class="{
            active: n === etape,
            done: n < etape,
            future: n > etape,
          }"
          :aria-label="`Étape ${n}`"
          :aria-current="n === etape ? 'step' : undefined"
          @click="n < etape ? $emit('go-to', n) : undefined"
        >
          <span v-if="n < etape" class="check">✓</span>
          <span v-else>{{ n }}</span>
        </button>
        <div v-if="n < 6" class="line" :class="{ done: n < etape }" />
      </div>
      <span
        v-if="labels?.[n - 1]"
        class="label"
        :class="{
          active: n === etape,
          done: n < etape,
        }"
      >
        {{ labels[n - 1] }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  etape: number
  vertical?: boolean
  labels?: string[]
}>()
defineEmits<{ (e: 'go-to', n: number): void }>()
</script>

<style scoped lang="scss">
// ─── Horizontal (default) ──────────────────────────────────────────────────────
.stepper {
  display: flex;
  align-items: center;
  padding: $spacing-m 0 $spacing-s;
  flex: 1;
  overflow: hidden;

  .item {
    display: flex;
    align-items: center;
    flex: 1;

    &:last-child {
      flex: 0;
    }
  }

  .dot-col {
    display: flex;
    align-items: center;
    flex: 1;
  }

  .dot {
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

    &.active {
      background: $brand-accent;
      border-color: $brand-accent;
      color: #fff;
    }

    &.done {
      background: $brand-accent;
      border-color: $brand-accent;
      color: #fff;
      cursor: pointer;

      &:hover {
        opacity: 0.85;
      }
    }

    &.future {
      background: #fff;
      border-color: $divider-color;
      color: $text-secondary;
      opacity: 0.5;
    }
  }

  .check {
    font-size: 0.7rem;
  }

  .line {
    flex: 1;
    height: 2px;
    background: $divider-color;
    transition: background 200ms ease;

    &.done {
      background: $brand-accent;
    }
  }

  .label {
    display: none;
  }

  // ─── Vertical ───────────────────────────────────────────────────────────────
  &.vertical {
    flex-direction: column;
    align-items: flex-start;
    padding: 0;
    overflow: visible;

    .item {
      flex: 0;
      align-items: flex-start;
      gap: $spacing-s;
    }

    .dot-col {
      flex: 0;
      flex-direction: column;
      align-items: center;
    }

    .line {
      flex: none;
      width: 2px;
      height: 1.5rem;
      background: $divider-color;

      &.done {
        background: $brand-accent;
      }
    }

    .label {
      display: block;
      padding-top: 0.3rem;
      font-size: 0.875rem;
      color: $text-secondary;
      line-height: 1.4;
      transition: color 200ms ease;

      &.active {
        color: $text-primary;
        font-weight: 600;
      }

      &.done {
        color: $text-secondary;
      }
    }
  }
}
</style>
