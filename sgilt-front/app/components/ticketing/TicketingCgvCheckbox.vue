<template>
  <div class="cgv-field">
    <label class="cgv-row">
      <input
        :checked="modelValue"
        type="checkbox"
        class="cgv-checkbox"
        @change="$emit('update:modelValue', ($event.target as HTMLInputElement).checked)"
      />
      <span class="cgv-label">
        {{ $t('ticketing.order.cgv-accept') }}
        <NuxtLink to="/m/cgu" target="_blank" class="cgv-link" @click.stop>{{
          $t('ticketing.order.cgv-link')
        }}</NuxtLink>
        <span class="required">*</span>
      </span>
    </label>
    <p v-if="error" class="field-error">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{ modelValue: boolean; error?: string | null }>()
defineEmits<{ 'update:modelValue': [value: boolean] }>()
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.cgv-field {
  display: flex;
  flex-direction: column;
  gap: $spacing-xs;
}

.cgv-row {
  display: flex;
  align-items: flex-start;
  gap: $spacing-s;
  cursor: pointer;
}

.cgv-checkbox {
  flex-shrink: 0;
  appearance: none;
  display: grid;
  place-content: center;
  width: 1.5rem;
  height: 1.5rem;
  margin: 0;
  border: 1.5px solid $divider-color;
  border-radius: $radius-sm;
  background: $surface-white;
  cursor: pointer;
  transition:
    background 150ms ease,
    border-color 150ms ease;

  &::after {
    content: '';
    width: 0.4rem;
    height: 0.75rem;
    border: solid $brand-primary;
    border-width: 0 2px 2px 0;
    transform: rotate(45deg) translate(-1px, -2px);
    opacity: 0;
  }

  &:checked {
    background: $brand-accent;
    border-color: $brand-accent;

    &::after {
      opacity: 1;
    }
  }
}

.cgv-label {
  font-size: $font-size-sm;
  color: $text-primary;

  .required {
    color: $state-error;
  }
}

.cgv-link {
  color: $text-primary;
  text-decoration: underline;
}

.field-error {
  margin: 0;
  font-size: $font-size-xs;
  color: $state-error;
}
</style>
