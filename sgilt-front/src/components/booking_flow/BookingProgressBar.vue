<template>
  <div class="progress-bar">
    <div
      v-for="stepItem in steps"
      :key="stepItem"
      :class="[
        'step',
        { active: stepItem === currentStep, completed: stepItem < currentStep!! },
        { 'last-step': stepItem === 4 },
      ]"
      @click="emit('stepChange', stepItem)"
    >
      <div class="step-circle">
        <span v-if="stepItem < currentStep!!">✔</span>
        <span v-else>{{ stepItem }}</span>
      </div>
      <div v-if="stepItem < 4" class="step-line"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
const currentStep = defineModel<number>()

const steps = [1, 2, 3, 4]

const emit = defineEmits<{
  (e: 'stepChange', step: number): void
}>()
</script>

<style scoped lang="scss">
.progress-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 1rem;
  padding: 0 1rem;
}

.step {
  display: flex;
  align-items: center;
  position: relative;
  &:not(.last-step) {
    flex: 1;
  }
}

.step-circle {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  background: $color-secondary;
  transition: 0.3s;
}

.step-line {
  flex: 1;
  height: 3px;
  background: $shadow-s;
}

.step.active .step-circle {
  background: $color-accent;
  color: white;
  box-shadow: 0 0 10px rgba(255, 191, 0, 0.5);
}

.step.completed .step-circle {
  background: $color-primary;
  color: white;
  cursor: pointer;
}

.step.completed .step-line {
  background: $color-primary;
}
</style>
