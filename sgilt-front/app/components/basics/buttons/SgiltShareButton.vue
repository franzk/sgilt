<template>
  <button class="share-button" type="button" @click="share" aria-label="Partager">
    <ShareIcon />
  </button>
</template>

<script setup lang="ts">
/**
 * Bouton partage superposé à une photo hero (fiche prestataire, page événement…) — partage natif
 * (Web Share API) avec repli sur la copie du lien dans le presse-papier.
 */
import { ShareIcon } from '@remixicons/vue/line'

const props = defineProps<{
  title: string
  text: string
}>()

async function share(): Promise<void> {
  if (navigator.share) {
    await navigator.share({
      title: props.title,
      text: props.text,
      url: window.location.href,
    })
  } else {
    await navigator.clipboard.writeText(window.location.href)
  }
}
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.share-button {
  position: absolute;
  top: $spacing-m;
  right: $spacing-m;
  z-index: 10;
  width: 2.2rem;
  height: 2.2rem;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.4);
  background: rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(6px);
  color: $text-inverted;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;

  svg {
    width: 1rem;
    height: 1rem;
  }
}
</style>
