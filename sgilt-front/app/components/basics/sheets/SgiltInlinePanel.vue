<template>
  <!-- ── Mobile : bottom sheet ────────────────────────────────────────────────── -->
  <SgiltBottomSheet v-if="!isDesktop" v-model:open="open" :title="title" overlay>
    <slot />
  </SgiltBottomSheet>

  <!-- ── Desktop : déroulé sur place, à l'endroit où le panneau est posé ─────── -->
  <Transition v-else name="sgilt-inline-panel">
    <div v-if="open" class="sgilt-inline-panel">
      <slot />
    </div>
  </Transition>
</template>

<script setup lang="ts">
// Panneau qui s'ouvre sur place sur desktop (sous l'élément qui le précède) et en bottom sheet
// sur mobile. Son contenu n'existe que panneau ouvert : il est recréé à chaque ouverture.
import SgiltBottomSheet from '~/components/basics/sheets/SgiltBottomSheet.vue'

const open = defineModel<boolean>('open', { required: true })

defineProps<{
  // Titre du bottom sheet (mobile).
  title: string
}>()

const { isDesktop } = useDevice()
</script>

<style scoped lang="scss">
.sgilt-inline-panel-enter-active,
.sgilt-inline-panel-leave-active {
  transition:
    opacity 200ms ease,
    transform 200ms ease;
}

.sgilt-inline-panel-enter-from,
.sgilt-inline-panel-leave-to {
  opacity: 0;
  transform: translateY(-0.5rem);
}
</style>
