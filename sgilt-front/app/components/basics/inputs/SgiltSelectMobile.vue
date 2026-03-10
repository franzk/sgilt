<template>
  <SgiltBottomSheet
    v-model:open="isOpen"
    overlay
    title="Qu'est-ce qu'on fête ?"
    description="Choisissez un type d'événement dans la liste."
  >
    <template #trigger>
      <button ref="triggerRef" type="button" class="select-trigger">
        <div class="trigger-content">
          <span class="left-icon"><slot name="left-icon" /></span>
          <span :class="{ 'has-value': !!modelValue && modelValue !== '-1' }" class="value">{{
            selectedLabel
          }}</span>
        </div>
      </button>
    </template>

    <div class="sheet-inner">
      <div class="sheet-handle" />

      <div class="sheet-body">
        <div class="sheet-header">
          <h3 aria-hidden="true">Qu'est-ce qu'on fête ?</h3>
        </div>

        <div class="options-container">
          <button
            v-for="option in options"
            :key="option.value"
            class="option-item"
            :class="{ 'is-selected': modelValue === option.value }"
            @click="selectOption(option.value)"
          >
            <span class="option-label">{{ option.label }}</span>
            <div v-if="modelValue === option.value" class="check-mark">✓</div>
          </button>
        </div>
      </div>
    </div>
  </SgiltBottomSheet>
</template>

<script setup lang="ts">
import SgiltBottomSheet from '~/components/basics/sheets/SgiltBottomSheet.vue'
import { nextTick, onMounted, ref } from 'vue'

const modelValue = defineModel<string>()

interface Props {
  options: { value: string; label: string }[]
}

const props = withDefaults(defineProps<Props>(), {
  options: () => [],
})

const isOpen = ref(false)
const triggerRef = ref<HTMLButtonElement | null>(null)

onMounted(() => {
  nextTick(() => {
    triggerRef.value?.blur()
  })
})

const selectedLabel = computed(() => {
  const option = props.options.find((o) => o.value === modelValue.value)
  return option ? option.label : ''
})

const selectOption = (val: string) => {
  modelValue.value = val
  isOpen.value = false
}
</script>

<style scoped lang="scss">
$primary: $color-primary;
$accent: $color-accent;
$sheet-radius: 28px;

$bg: #ffffff;
$muted: #f6f7f9;
$muted2: #eef0f3;

$shadow-sheet: 0 -28px 80px rgba(0, 0, 0, 0.28);
$shadow-pop: 0 10px 28px rgba(0, 0, 0, 0.1);

// ─── Sheet inner wrapper ───────────────────────────────────────────────────────
.sheet-inner {
  background:
    radial-gradient(
      1100px 520px at 50% -10%,
      rgba($color-accent, 0.22) 0%,
      rgba(255, 255, 255, 0) 55%
    ),
    linear-gradient(180deg, #fffdf6 0%, #ffffff 60%);

  border-top: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: $shadow-sheet;
  padding-bottom: calc(1.1rem + env(safe-area-inset-bottom, 0px));
}

.sheet-handle {
  width: 52px;
  height: 6px;
  margin: 12px auto 10px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.14);
}

.sheet-body {
  padding: 0.9rem 1.2rem 0.9rem;
}

.sheet-header {
  margin: 0.25rem 0 1rem;

  h3 {
    margin: 0.25rem 0 0.9rem;
    text-align: center;

    font-size: 1.45rem;
    font-weight: 700;
    letter-spacing: -0.025em;
    line-height: 1.1;

    color: $color-primary;

    text-shadow:
      0 1px 0 rgba(255, 255, 255, 0.7),
      0 10px 26px rgba(0, 0, 0, 0.1);
  }

  h3::after {
    content: '';
    display: block;
    width: 72px;
    height: 4px;
    margin: 0.65rem auto 0;

    border-radius: 999px;
    background: linear-gradient(
      90deg,
      rgba($color-accent, 0) 0%,
      rgba($color-accent, 0.85) 20%,
      rgba($color-accent, 1) 50%,
      rgba($color-accent, 0.85) 80%,
      rgba($color-accent, 0) 100%
    );
    box-shadow: 0 10px 18px rgba($color-accent, 0.18);
  }
}

.options-container {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

// ─── Option button ─────────────────────────────────────────────────────────────
.option-item {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;

  padding: 1.05rem 1rem 1.05rem 1.1rem;
  border-radius: 18px;

  background: linear-gradient(180deg, #ffffff 0%, #fbfbfb 100%);
  border: 1px solid rgba(0, 0, 0, 0.08);

  box-shadow:
    0 14px 34px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.85);

  color: $color-primary;
  font-weight: 750;
  font-size: 1rem;

  transition:
    transform 120ms ease,
    box-shadow 160ms ease,
    border-color 160ms ease;

  -webkit-tap-highlight-color: transparent;

  &:hover {
    border-color: rgba($color-accent, 0.22);
    box-shadow:
      0 14px 30px rgba(0, 0, 0, 0.08),
      0 0 0 3px rgba($color-accent, 0.1);
  }

  &:active {
    transform: scale(0.985);
    box-shadow:
      0 10px 24px rgba(0, 0, 0, 0.1),
      0 0 0 4px rgba($color-accent, 0.14);
  }

  &:focus-visible {
    outline: none;
    border-color: rgba($color-accent, 0.55);
    box-shadow:
      0 0 0 5px rgba($color-accent, 0.2),
      0 18px 40px rgba(0, 0, 0, 0.12);
  }

  .check-mark {
    min-width: 32px;
    height: 32px;
    border-radius: 999px;
    display: inline-flex;
    align-items: center;
    justify-content: center;

    font-weight: 950;
    font-size: 0.95rem;

    background: rgba(0, 0, 0, 0.06);
    border: 1px solid rgba(0, 0, 0, 0.1);
    color: rgba($color-primary, 0.8);
  }

  &.is-selected {
    background: linear-gradient(180deg, rgba($color-accent, 1) 0%, rgba($color-accent, 0.86) 100%);
    border-color: rgba($color-accent, 0.65);

    color: #111;

    box-shadow:
      0 22px 50px rgba($color-accent, 0.26),
      0 14px 34px rgba(0, 0, 0, 0.1);

    &::before {
      background: rgba(0, 0, 0, 0.18);
      opacity: 1;
    }

    .check-mark {
      background: rgba(0, 0, 0, 0.14);
      border-color: rgba(0, 0, 0, 0.16);
      color: #111;
    }
  }
}

// ─── Trigger ───────────────────────────────────────────────────────────────────
.select-trigger {
  width: 100%;
  cursor: pointer;
  background: transparent;
  border: none;
  padding: 0;
}

.trigger-content {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.left-icon {
  display: flex;
  align-items: center;
}

.value {
  color: $color-subtext;

  &.has-value {
    color: $color-primary;
    font-weight: 500;
  }
}
</style>
