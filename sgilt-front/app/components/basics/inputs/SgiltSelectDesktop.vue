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

$dropdown-border-radius: 1.4rem;
$dropdown-border: 1px solid rgba(0, 0, 0, 0.08);
$dropdown-box-shadow: 0 18px 44px rgba(0, 0, 0, 0.14);
$dropdown-background: white;
$dropdown-width: 17rem;

/* Dropdown */
.select-content {
  z-index: 2000 !important;
  border-radius: $dropdown-border-radius;
  background: $dropdown-background;
  border: $dropdown-border;
  box-shadow: $dropdown-box-shadow;
  overflow: hidden;
  width: $dropdown-width;
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

.chev {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0.55;
  user-select: none;
}
</style>
