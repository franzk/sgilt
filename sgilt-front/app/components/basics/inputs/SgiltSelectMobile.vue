<template>
  <SgiltBottomSheet v-model:open="isOpen" :title="title" overlay>
    <template #trigger>
      <button ref="triggerRef" type="button" class="select-trigger">
        <div class="trigger-content">
          <span class="left-icon"><slot name="left-icon" /></span>
          <span :class="{ 'has-value': !!modelValue && modelValue !== '-1' }" class="value">
            {{ selectedLabel }}
          </span>
        </div>
      </button>
    </template>

    <div class="options-wrapper">
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
  </SgiltBottomSheet>
</template>

<script setup lang="ts">
import SgiltBottomSheet from '~/components/basics/sheets/SgiltBottomSheet.vue'

const modelValue = defineModel<string>()

interface Props {
  options: { value: string; label: string }[]
  title?: string
}

const props = withDefaults(defineProps<Props>(), {
  options: () => [],
  title: "Qu'est-ce qu'on fête ?",
})

const isOpen = ref(false)
const triggerRef = ref<HTMLButtonElement | null>(null)

onMounted(() => nextTick(() => triggerRef.value?.blur()))

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
// ── Options ────────────────────────────────────────────────────────────────────
.options-wrapper {
  overflow-y: auto;
  flex: 1;
  padding: $spacing-xs $spacing-m calc($spacing-l + env(safe-area-inset-bottom, 0px));
}

.options-container {
  display: flex;
  flex-direction: column;
  gap: $spacing-s;
}

.option-item {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1rem 1rem 1.1rem;
  border-radius: 16px;
  background: linear-gradient(180deg, #ffffff 0%, #fbfbfb 100%);
  border: 1px solid rgba(0, 0, 0, 0.07);
  box-shadow:
    0 2px 8px rgba(0, 0, 0, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  color: $brand-primary;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition:
    transform 120ms ease,
    box-shadow 150ms ease,
    border-color 150ms ease;
  -webkit-tap-highlight-color: transparent;

  &:active {
    transform: scale(0.985);
  }

  &:focus-visible {
    outline: none;
    border-color: rgba($brand-accent, 0.55);
    box-shadow: 0 0 0 4px rgba($brand-accent, 0.18);
  }

  &.is-selected {
    background: linear-gradient(180deg, $brand-accent 0%, rgba($brand-accent, 0.88) 100%);
    border-color: rgba($brand-accent, 0.5);
    box-shadow: 0 4px 16px rgba($brand-accent, 0.3);

    .check-mark {
      background: rgba(0, 0, 0, 0.12);
      border-color: rgba(0, 0, 0, 0.14);
      color: #111;
    }
  }
}

.check-mark {
  min-width: 28px;
  height: 28px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.85rem;
  background: rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(0, 0, 0, 0.08);
  color: rgba($brand-primary, 0.7);
}

// ── Trigger ────────────────────────────────────────────────────────────────────
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
  color: $text-secondary;

  &.has-value {
    color: $brand-primary;
    font-weight: 500;
  }
}
</style>
