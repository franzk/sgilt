<script setup lang="ts">
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import IconShare from '~/components/icons/IconShare.vue'
import type { PrestataireDetail } from '~/types/prestataire'

const props = defineProps<{
  prestataire: PrestataireDetail
}>()

const emit = defineEmits<{
  openVideo: []
}>()

// ─── Carousel ─────────────────────────────────────────────────────────────────
type HeroImage = { type: 'image'; src: string }
type HeroVideo = { type: 'video'; youtubeId: string }
type HeroItem = HeroImage | HeroVideo

const heroItems = computed<HeroItem[]>(() => {
  const images: HeroItem[] = [props.prestataire.heroImage, ...props.prestataire.photos].map(
    (src) => ({ type: 'image', src }),
  )
  if (props.prestataire.youtubeId) {
    return [...images, { type: 'video', youtubeId: props.prestataire.youtubeId }]
  }
  return images
})

const heroIndex = ref(0)
const touchStartX = ref(0)

function onTouchStart(e: TouchEvent) {
  if (!e.touches[0]) return
  touchStartX.value = e.touches[0].clientX
}

function onTouchEnd(e: TouchEvent) {
  const diff = touchStartX.value - (e.changedTouches[0]?.clientX ?? 0)
  if (Math.abs(diff) < 30) return
  if (diff > 0) heroIndex.value = (heroIndex.value + 1) % heroItems.value.length
  else heroIndex.value = (heroIndex.value - 1 + heroItems.value.length) % heroItems.value.length
}

// ─── Share ────────────────────────────────────────────────────────────────────
async function share() {
  if (navigator.share) {
    await navigator.share({
      title: props.prestataire.name,
      text: props.prestataire.baseline,
      url: window.location.href,
    })
  } else {
    await navigator.clipboard.writeText(window.location.href)
  }
}
</script>

<template>
  <section class="hero" @touchstart.passive="onTouchStart" @touchend.passive="onTouchEnd">
    <!-- Slide image -->
    <div v-if="heroItems[heroIndex]?.type === 'image'" class="hero__image">
      <SgiltImage
        :src="(heroItems[heroIndex] as HeroImage).src"
        :alt="prestataire.name"
        loading="eager"
      />
    </div>

    <!-- Slide vidéo -->
    <template v-else-if="heroItems[heroIndex]?.type === 'video'">
      <div class="hero__image">
        <SgiltImage
          :src="`https://img.youtube.com/vi/${(heroItems[heroIndex] as HeroVideo).youtubeId}/hqdefault.jpg`"
          alt="Vidéo"
          loading="eager"
        />
      </div>
      <button class="hero__video-play" @click="emit('openVideo')" aria-label="Lancer la vidéo">
        ▶
      </button>
    </template>

    <!-- Bouton share -->
    <button class="hero__share" @click="share" aria-label="Partager">
      <IconShare />
    </button>

    <!-- Overlay gradient -->
    <div class="hero__overlay" aria-hidden="true" />

    <!-- Contenu -->
    <div class="hero__content">
      <p class="hero__category">{{ prestataire.category }}</p>
      <h1 class="hero__name">{{ prestataire.name }}</h1>
      <p class="hero__baseline">{{ prestataire.baseline }}</p>
    </div>

    <!-- Dots -->
    <div v-if="heroItems.length > 1" class="hero__dots" aria-hidden="true">
      <span
        v-for="(_, i) in heroItems"
        :key="i"
        class="hero__dot"
        :class="{ 'hero__dot--active': i === heroIndex }"
      />
    </div>
  </section>
</template>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.hero {
  position: relative;
  width: 100%;
  aspect-ratio: 1 / 1;
  overflow: hidden;

  &__image {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center;
  }

  &__overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(
      to bottom,
      transparent 30%,
      rgba(0, 0, 0, 0.25) 60%,
      rgba(0, 0, 0, 0.72) 100%
    );
  }

  &__content {
    position: absolute;
    bottom: 1.5rem;
    left: 0;
    right: 0;
    padding: 0 $spacing-m;
    color: #fff;
    text-shadow: 0 2px 12px rgba(0, 0, 0, 0.4);
  }

  &__category {
    font-size: 0.8rem;
    font-weight: 600;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    opacity: 0.85;
    margin: 0 0 0.4rem;
    color: $color-accent;
  }

  &__name {
    font-family: 'Cormorant Garamond', serif;
    font-size: clamp(2rem, 6vw, 3.2rem);
    font-weight: 700;
    line-height: 1.1;
    margin: 0 0 0.5rem;
  }

  &__baseline {
    font-size: 1rem;
    font-weight: 400;
    opacity: 0.9;
    margin: 0;
    max-width: 36rem;
  }

  &__share {
    position: absolute;
    top: $spacing-m;
    right: $spacing-m;
    z-index: 2;
    width: 2.2rem;
    height: 2.2rem;
    border-radius: 50%;
    border: 1px solid rgba(255, 255, 255, 0.4);
    background: rgba(0, 0, 0, 0.25);
    backdrop-filter: blur(6px);
    color: #fff;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;

    svg {
      width: 1rem;
      height: 1rem;
    }
  }

  &__video-play {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 2;
    width: 3.5rem;
    height: 3.5rem;
    border-radius: 50%;
    border: 2px solid rgba(255, 255, 255, 0.8);
    background: rgba(0, 0, 0, 0.45);
    color: #fff;
    font-size: 1.2rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(4px);
    transition: background 150ms ease;

    &:hover {
      background: rgba(0, 0, 0, 0.65);
    }
  }

  &__dots {
    position: absolute;
    bottom: $spacing-s;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 0.35rem;
  }

  &__dot {
    width: 5px;
    height: 5px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.5);
    transition:
      background 200ms ease,
      transform 200ms ease;

    &--active {
      background: #fff;
      transform: scale(1.3);
    }
  }
}
</style>
