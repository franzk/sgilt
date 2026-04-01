<script setup lang="ts">
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import { ShareIcon, ArrowLeftIcon } from '@remixicons/vue/line'
import type { PrestataireDetail } from '~/types/prestataire'

const router = useRouter()

const props = defineProps<{
  prestataire: PrestataireDetail
}>()

const emit = defineEmits<{
  openVideo: []
  openPhoto: [index: number]
  back: []
}>()

// ─── Carousel mobile ──────────────────────────────────────────────────────────
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

// ─── Mosaïque desktop ─────────────────────────────────────────────────────────
type MosaicThumb =
  | { type: 'photo'; src: string; photoIndex: number }
  | { type: 'video'; src: string }

const mosaicThumbs = computed<MosaicThumb[]>(() => {
  const thumbs: MosaicThumb[] = props.prestataire.photos.map((src, i) => ({
    type: 'photo',
    src,
    photoIndex: i + 1, // 0 = heroImage dans la galerie
  }))
  if (props.prestataire.youtubeId) {
    thumbs.push({
      type: 'video',
      src: `https://img.youtube.com/vi/${props.prestataire.youtubeId}/hqdefault.jpg`,
    })
  }
  return thumbs
})

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
    <!-- ── Mobile : carousel ── -->
    <div class="carousel">
      <div v-if="heroItems[heroIndex]?.type === 'image'" class="image">
        <SgiltImage
          :src="(heroItems[heroIndex] as HeroImage).src"
          :alt="prestataire.name"
          loading="eager"
        />
      </div>

      <template v-else-if="heroItems[heroIndex]?.type === 'video'">
        <div class="image">
          <SgiltImage
            :src="`https://img.youtube.com/vi/${(heroItems[heroIndex] as HeroVideo).youtubeId}/hqdefault.jpg`"
            alt="Vidéo"
            loading="eager"
          />
        </div>
        <button class="video-play" @click="emit('openVideo')" aria-label="Lancer la vidéo">
          ▶
        </button>
      </template>

      <div class="overlay" aria-hidden="true" />

      <div class="content">
        <p class="category">{{ prestataire.category }}</p>
        <h1 class="name">{{ prestataire.name }}</h1>
        <p class="baseline">{{ prestataire.baseline }}</p>
      </div>

      <div v-if="heroItems.length > 1" class="dots" aria-hidden="true">
        <span
          v-for="(_, i) in heroItems"
          :key="i"
          class="dot"
          :class="{ active: i === heroIndex }"
        />
      </div>
    </div>

    <!-- ── Desktop : mosaïque ── -->
    <div class="mosaic">
      <!-- Photo principale -->
      <div class="mosaic-main">
        <SgiltImage :src="prestataire.heroImage" :alt="prestataire.name" loading="eager" />
        <div class="overlay" aria-hidden="true" />
        <div class="content">
          <p class="category">{{ prestataire.category }}</p>
          <h1 class="name">{{ prestataire.name }}</h1>
          <p class="baseline">{{ prestataire.baseline }}</p>
        </div>
      </div>

      <!-- Miniatures -->
      <div v-if="mosaicThumbs.length" class="mosaic-thumbs">
        <button
          v-for="(thumb, i) in mosaicThumbs"
          :key="i"
          class="mosaic-thumb"
          :class="{ video: thumb.type === 'video' }"
          @click="
            thumb.type === 'video'
              ? emit('openVideo')
              : emit(
                  'openPhoto',
                  (thumb as { type: 'photo'; src: string; photoIndex: number }).photoIndex,
                )
          "
          :aria-label="thumb.type === 'video' ? 'Lancer la vidéo' : `Voir la photo`"
        >
          <SgiltImage :src="thumb.src" alt="" loading="lazy" />
          <div v-if="thumb.type === 'video'" class="mosaic-play" aria-hidden="true">▶</div>
        </button>
      </div>
    </div>

    <!-- Bouton back (toujours visible) -->
    <button class="back" @click="emit('back')" aria-label="Retour">
      <ArrowLeftIcon />
    </button>

    <!-- Bouton share (toujours visible) -->
    <button class="share" @click="share" aria-label="Partager">
      <ShareIcon />
    </button>
  </section>
</template>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ─── Section wrapper ───────────────────────────────────────────────────────────
.hero {
  position: relative;
  width: 100%;

  // ─── Mobile : carousel ────────────────────────────────────────────────────────
  .carousel {
    position: relative;
    width: 100%;
    aspect-ratio: 1 / 1;
    overflow: hidden;

    @media (min-width: $breakpoint-desktop) {
      display: none;
    }
  }

  .image {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center;
  }

  // ─── Desktop : mosaïque ───────────────────────────────────────────────────────
  .mosaic {
    display: none;

    @media (min-width: $breakpoint-desktop) {
      display: grid;
      grid-template-columns: 1.2fr 1fr;
      height: 55vh;
      gap: 8px;
      overflow: hidden;
    }

    &-main {
      position: relative;
      overflow: hidden;
      height: 100%;
      border-radius: 1rem;
    }

    &-thumbs {
      display: grid;
      grid-template-columns: 1fr 1fr;
      grid-template-rows: 1fr 1fr;
      gap: 0.5rem;
      height: 100%;
      overflow: hidden;
    }

    &-thumb {
      position: relative;
      flex: 1;
      overflow: hidden;
      border: none;
      padding: 0;
      cursor: pointer;
      display: block;
      border-radius: 1rem;

      &::after {
        content: '';
        position: absolute;
        inset: 0;
        background: rgba(0, 0, 0, 0);
        transition: background 150ms ease;
        z-index: 1;
      }

      &:hover::after {
        background: rgba(0, 0, 0, 0.18);
      }
    }

    &-play {
      position: absolute;
      inset: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(0, 0, 0, 0.38);
      color: #fff;
      font-size: 1.1rem;
      pointer-events: none;
      z-index: 2;
    }
  }

  // ─── Éléments partagés (overlay + contenu) ────────────────────────────────────
  .overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(
      to bottom,
      transparent 30%,
      rgba(0, 0, 0, 0.25) 60%,
      rgba(0, 0, 0, 0.72) 100%
    );
  }

  .content {
    position: absolute;
    bottom: 1.5rem;
    left: 0;
    right: 0;
    padding: 0 $spacing-m;
    color: #fff;
    text-shadow: 0 2px 12px rgba(0, 0, 0, 0.4);
  }

  .category {
    font-size: 0.8rem;
    font-weight: 600;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    opacity: 0.85;
    margin: 0 0 0.4rem;
    color: $color-accent;
  }

  .name {
    font-family: 'Cormorant Garamond', serif;
    font-size: clamp(2rem, 6vw, 3.2rem);
    font-weight: 700;
    line-height: 1.1;
    margin: 0 0 0.5rem;
  }

  .baseline {
    font-size: 1rem;
    font-weight: 400;
    opacity: 0.9;
    margin: 0;
    max-width: 36rem;
  }

  // ─── Back & Share ─────────────────────────────────────────────────────────────
  %overlay-btn {
    position: absolute;
    top: $spacing-m;
    z-index: 10;
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

  .back {
    @extend %overlay-btn;
    left: $spacing-m;
  }

  .share {
    @extend %overlay-btn;
    right: $spacing-m;
  }

  // ─── Bouton play (carousel mobile) ───────────────────────────────────────────
  .video-play {
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

  // ─── Dots (carousel mobile) ───────────────────────────────────────────────────
  .dots {
    position: absolute;
    bottom: $spacing-s;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 0.35rem;
  }

  .dot {
    width: 5px;
    height: 5px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.5);
    transition:
      background 200ms ease,
      transform 200ms ease;

    &.active {
      background: #fff;
      transform: scale(1.3);
    }
  }
}
</style>
