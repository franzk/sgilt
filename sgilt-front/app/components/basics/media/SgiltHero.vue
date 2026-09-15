<script setup lang="ts">
/**
 * Hero photo générique (carousel mobile / mosaïque desktop + bandeau texte superposé),
 * factorisé pour être partagé entre la fiche prestataire (PrestataireHero.vue) et les pages
 * événement publiques (billetterie) — même galerie, même taille, même typographie. Les boutons
 * retour et partage sont natifs (props `showBack`/`showShare` + emit `back`), le reste des
 * affordances spécifiques au domaine (édition...) passe par les slots `top-right` / `empty-actions`,
 * à la charge de l'appelant.
 */
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import SgiltHeroOverlay from '~/components/basics/media/SgiltHeroOverlay.vue'
import SgiltShareButton from '~/components/basics/buttons/SgiltShareButton.vue'
import { ArrowLeftIcon } from '@remixicons/vue/line'

export interface HeroMedia {
  type: 'IMAGE' | 'YOUTUBE'
  ref: string
  position: number
}

const props = defineProps<{
  medias: HeroMedia[]
  tag?: string
  title: string
  /** Affiche le bouton retour (haut gauche) — émet `back` au clic plutôt que de naviguer lui-même. */
  showBack?: boolean
  /** Affiche le bouton partager (haut droite) — partage `title` (identique au titre du hero). */
  showShare?: boolean
  /** Texte du partage (Web Share API) — requis quand `showShare` est vrai. */
  shareText?: string
}>()

const emit = defineEmits<{
  openVideo: [youtubeId: string]
  openPhoto: [index: number]
  back: []
}>()

const hasMedia = computed(() => props.medias.length > 0)

// ─── Carousel mobile ──────────────────────────────────────────────────────────
type HeroImage = { type: 'image'; src: string }
type HeroVideo = { type: 'video'; youtubeId: string }
type HeroItem = HeroImage | HeroVideo

const heroItems = computed<HeroItem[]>(() =>
  props.medias.map((m) =>
    m.type === 'IMAGE' ? { type: 'image', src: m.ref } : { type: 'video', youtubeId: m.ref },
  ),
)

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
  | { type: 'video'; src: string; youtubeId: string }

const imageMedias = computed(() => props.medias.filter((m) => m.type === 'IMAGE'))

const mosaicThumbs = computed<MosaicThumb[]>(() =>
  props.medias.slice(1).map((m) => {
    if (m.type === 'YOUTUBE') {
      return { type: 'video', src: youtubeThumbnailUrl(m.ref), youtubeId: m.ref }
    }
    const photoIndex = imageMedias.value.findIndex((img) => img.position === m.position)
    return { type: 'photo', src: m.ref, photoIndex: photoIndex === -1 ? 0 : photoIndex }
  }),
)

const mainImageSrc = computed(() => heroRef(props.medias) ?? '')

function onThumbClick(thumb: MosaicThumb): void {
  if (thumb.type === 'video') {
    emit('openVideo', thumb.youtubeId)
  } else {
    emit('openPhoto', thumb.photoIndex)
  }
}
</script>

<template>
  <section
    class="sgilt-hero"
    :class="{ 'no-media': !hasMedia }"
    @touchstart.passive="onTouchStart"
    @touchend.passive="onTouchEnd"
  >
    <template v-if="hasMedia">
      <!-- ── Mobile : carousel ── -->
      <div class="carousel">
        <div v-if="heroItems[heroIndex]?.type === 'image'" class="image">
          <SgiltImage :src="(heroItems[heroIndex] as HeroImage).src" :alt="title" loading="eager" />
        </div>

        <template v-else-if="heroItems[heroIndex]?.type === 'video'">
          <div class="image">
            <SgiltImage
              :src="youtubeThumbnailUrl((heroItems[heroIndex] as HeroVideo).youtubeId)"
              alt="Vidéo"
              loading="eager"
            />
          </div>
          <button
            class="video-play"
            @click="emit('openVideo', (heroItems[heroIndex] as HeroVideo).youtubeId)"
            aria-label="Lancer la vidéo"
          >
            ▶
          </button>
        </template>

        <SgiltHeroOverlay :tag="tag" :title="title">
          <template #subtitle><slot name="subtitle" /></template>
          <slot />
        </SgiltHeroOverlay>

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
      <div class="mosaic" :class="{ single: !mosaicThumbs.length }">
        <!-- Photo principale -->
        <div class="mosaic-main">
          <SgiltImage :src="mainImageSrc" :alt="title" loading="eager" />
          <SgiltHeroOverlay :tag="tag" :title="title">
            <template #subtitle><slot name="subtitle" /></template>
            <slot />
          </SgiltHeroOverlay>
        </div>

        <!-- Miniatures -->
        <div v-if="mosaicThumbs.length" class="mosaic-thumbs">
          <button
            v-for="(thumb, i) in mosaicThumbs"
            :key="i"
            class="mosaic-thumb"
            :class="{ video: thumb.type === 'video' }"
            @click="onThumbClick(thumb)"
            :aria-label="thumb.type === 'video' ? 'Lancer la vidéo' : `Voir la photo`"
          >
            <SgiltImage :src="thumb.src" alt="" loading="lazy" />
            <div v-if="thumb.type === 'video'" class="mosaic-play" aria-hidden="true">▶</div>
          </button>
        </div>
      </div>
    </template>

    <!-- ── État sans média : pas de placeholder d'image, hero réduit au texte ── -->
    <div v-else class="empty-hero">
      <div class="content">
        <p v-if="tag" class="category">{{ tag }}</p>
        <h1 class="name">{{ title }}</h1>
        <div class="baseline">
          <slot name="subtitle" />
        </div>
      </div>

      <slot name="empty-actions" />
    </div>

    <button v-if="showBack" class="back" type="button" @click="emit('back')" aria-label="Retour">
      <ArrowLeftIcon />
    </button>

    <SgiltShareButton v-if="showShare" :title="title" :text="shareText ?? ''" />

    <slot name="top-right" />
  </section>
</template>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

.sgilt-hero {
  position: relative;
  width: 100%;

  // ─── Mobile : carousel ────────────────────────────────────────────────────────
  .carousel {
    position: relative;
    width: 100%;
    aspect-ratio: 3 / 2;
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

      // Un seul média (pas de miniatures) : la photo principale occupe toute la largeur.
      &.single {
        grid-template-columns: 1fr;
      }
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

  // ─── État sans média : hero réduit au texte, pas de placeholder d'image ───────
  .empty-hero {
    position: relative;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: $spacing-m;
    padding: $spacing-xxl $spacing-m $spacing-l;
    background: $surface-soft;

    @media (min-width: $breakpoint-desktop) {
      align-items: center;
      text-align: center;
      padding: $spacing-xxl $spacing-l;
    }

    .category {
      font-size: 0.8rem;
      font-weight: 600;
      letter-spacing: 0.12em;
      text-transform: uppercase;
      opacity: 0.85;
      margin: 0 0 0.4rem;
      color: $brand-accent;
    }

    .name {
      font-family: 'Cormorant Garamond', serif;
      font-size: clamp(2rem, 6vw, 3.2rem);
      font-weight: 700;
      line-height: 1.1;
      margin: 0 0 0.5rem;
      color: $brand-primary;
    }

    .baseline {
      font-size: 1rem;
      font-weight: 400;
      opacity: 0.9;
      margin: 0;
      max-width: 36rem;
      color: $text-secondary;
    }
  }

  // ─── Bouton retour ────────────────────────────────────────────────────────────
  .back {
    position: absolute;
    top: $spacing-m;
    left: $spacing-m;
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
