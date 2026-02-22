<template>
  <SelectRoot v-model="modelValue">
    <SelectTrigger asChild>
      <button ref="triggerRef" type="button" class="select-trigger">
        <div class="trigger-content">
          <span class="left-icon"><slot name="left-icon" /></span>
          <SelectValue
            class="value"
            :class="{ 'has-value': !!modelValue && modelValue !== '-1' }"
            placeholder="placeholder"
          />
        </div>

        <span class="chev">▾</span>
      </button>
    </SelectTrigger>

    <SelectPortal>
      <SelectContent asChild position="popper" :side-offset="8" :avoid-collisions="true">
        <div class="select-content">
          <SelectViewport class="select-viewport">
            <SelectItem v-for="o in options" :key="o.value" class="select-item" :value="o.value">
              <SelectItemText class="item-text">{{ o.label }}</SelectItemText>
              <SelectItemIndicator class="item-check">✓</SelectItemIndicator>
            </SelectItem>
          </SelectViewport>
        </div>
      </SelectContent>
    </SelectPortal>
  </SelectRoot>
</template>

<script setup lang="ts">
import {
  SelectRoot,
  SelectTrigger,
  SelectValue,
  SelectPortal,
  SelectContent,
  SelectViewport,
  SelectItem,
  SelectItemText,
  SelectItemIndicator,
} from 'reka-ui'
import { nextTick, onMounted, ref } from 'vue'

const modelValue = defineModel<string>()

interface Props {
  options: { value: string; label: string }[]
}

const props = withDefaults(defineProps<Props>(), {
  options: () => [],
})

const triggerRef = ref<HTMLButtonElement | null>(null)

onMounted(() => {
  nextTick(() => {
    triggerRef.value?.blur()
  })
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

/* .select-trigger {
  width: 100%;
  height: 3.5rem;
  border-radius: 0.875rem;
  background: white;
  border: 1px solid $shadow-m;
  box-shadow:
    0 0.0625rem 0 rgba(0, 0, 0, 0.04),
    0 0.5rem 1.25rem rgba(0, 0, 0, 0.05);

  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 0.5rem;
  gap: 0.75rem;

  cursor: pointer;
  -webkit-tap-highlight-color: transparent;

  &:focus-visible {
    outline: none;
    border-color: rgba($color-accent, 0.55);
    box-shadow:
      0 10px 24px rgba(0, 0, 0, 0.06),
      0 0 0 4px rgba($color-accent, 0.22);
  }
}

.trigger-content {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
  min-width: 0;
  color: $text-secondary;
}

.value {
  flex: 1;
  min-width: 0;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.has-value {
  color: $text-primary;
  font-weight: 600;
}

.chev {
  opacity: 0.55;
  user-select: none;
} */

.chev {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0.55;
  user-select: none;
}

/* Dropdown */
.select-content {
  z-index: 2000 !important;
  border-radius: 16px;
  background: white;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 18px 44px rgba(0, 0, 0, 0.14);
  overflow: hidden;
  width: 17rem;
}

.select-viewport {
  padding: 0.5rem;
  max-height: 320px;
}

.select-item {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;

  border-radius: 12px;
  padding: 0.85rem 0.9rem;

  cursor: pointer;
  user-select: none;
  color: $color-primary;
  font-weight: 550;

  &:hover {
    background: rgba($color-accent, 0.12);
  }

  &[data-highlighted] {
    background: rgba($color-accent, 0.16);
  }

  &[data-disabled] {
    opacity: 0.45;
    cursor: not-allowed;
  }
}

.item-check {
  opacity: 0;
}

.select-item[data-state='checked'] .item-check {
  opacity: 1;
}
</style>
