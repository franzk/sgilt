<template>
  <SgiltChip
    :label="iconOnly ? '' : $t(`categories.${categoryName}.title`)"
    @click="$emit('click')"
    class="category-chip small-font"
  >
    <!--component :is="icon" class="icon" /-->
    <SgiltIcon :icon="icon" class="icon" />
  </SgiltChip>
</template>

<script setup lang="ts">
import SgiltIcon from '@/components/basics/icons/SgiltIcon.vue'
import SgiltChip from '@/components/basics/chips/SgiltChip.vue'
import { computed } from 'vue'

const props = defineProps<{
  categoryName: string
  iconOnly?: boolean
}>()

defineEmits(['click'])

const icon = computed(
  () => props.categoryName.charAt(0).toUpperCase() + props.categoryName.slice(1),
)
</script>

<style scoped lang="scss">
.category-chip {
  cursor: pointer;
  color: $color-secondary;
  background-color: $shadow-l;
  border: $border-width-s solid $color-secondary;
  gap: $spacing-xs;
  margin: $spacing-s;

  svg {
    height: 100%;
    aspect-ratio: 1;
  }
}
</style>
