<template>
  <div class="checkbox-wrapper">
    <label>
      <input type="checkbox" v-model="selection" :id="id" :value="value" />
      <span class="checkbox"></span>
      <label :for="id">{{ label }}</label>
    </label>
  </div>
</template>

<script setup lang="ts">
import type { SearchCategoryTag } from '@/types/SearchCategory'

const selection = defineModel<SearchCategoryTag[]>()

defineProps<{
  id: string
  value: SearchCategoryTag
  label: string
}>()
</script>

<style lang="scss">
.checkbox-wrapper {
  --borderColor: #{$color-accent};
  --borderWidth: #{$border-width-m};
  --BackgroundColor: #{$color-secondary};
  --checkColor: #{$color-secondary};
  --checkedBackgroundColor: #{$color-accent};
}

.checkbox-wrapper label {
  display: flex;
  flex-direction: row;
  gap: $spacing-xs;
  align-items: center;
  cursor: pointer;
}

.checkbox-wrapper input[type='checkbox'] {
  -webkit-appearance: none;
  appearance: none;
  vertical-align: middle;
  background: var(--BackgroundColor);
  font-size: 1.8em;
  border-radius: 0.125em;
  display: inline-block;
  border: var(--borderWidth) solid var(--borderColor);
  width: 0.8em;
  height: 0.8em;
  position: relative;
}
.checkbox-wrapper input[type='checkbox']:before,
.checkbox-wrapper input[type='checkbox']:after {
  content: '';
  position: absolute;
  background: var(--borderColor);
  width: calc(var(--borderWidth) * 3);
  height: var(--borderWidth);
  top: 50%;
  left: 10%;
  transform-origin: left center;
}
.checkbox-wrapper input[type='checkbox']:before {
  transform: rotate(45deg) translate(calc(var(--borderWidth) / -2), calc(var(--borderWidth) / -2))
    scaleX(0);
  transition: transform 200ms ease-in 200ms;
}
.checkbox-wrapper input[type='checkbox']:after {
  width: calc(var(--borderWidth) * 5);
  transform: rotate(-45deg) translateY(calc(var(--borderWidth) * 2)) scaleX(0);
  transform-origin: left center;
  transition: transform 200ms ease-in;
}
.checkbox-wrapper input[type='checkbox']:checked {
  background: var(--checkedBackgroundColor);
}
.checkbox-wrapper input[type='checkbox']:checked:before {
  transform: rotate(45deg) translate(calc(var(--borderWidth) / -2), calc(var(--borderWidth) / -2))
    scaleX(1);
  background: var(--checkColor);
  transition: transform 200ms ease-in;
  margin-left: 2px;
}
.checkbox-wrapper input[type='checkbox']:checked:after {
  width: calc(var(--borderWidth) * 5);
  transform: rotate(-45deg) translateY(calc(var(--borderWidth) * 2)) scaleX(1);
  background: var(--checkColor);
  transition: transform 200ms ease-out 200ms;
  margin-left: 2px;
}
</style>
