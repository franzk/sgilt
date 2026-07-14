<script setup lang="ts">
import SgiltImage from '~/components/basics/media/SgiltImage.vue'
import EditableText from '~/components/prestataire/EditableText.vue'
import PrestataireMediaDialog from '~/components/prestataire/PrestataireMediaDialog.vue'
import { ShareIcon, ArrowLeftIcon, ImageAddIcon } from '@remixicons/vue/line'
import type { PrestataireDetail } from '~/data/prestataire/domain/PrestataireDetail'
import type { DisplayMode } from '~/types/prestataire'

const router = useRouter()
const { t } = useI18n()

const props = defineProps<{
  prestataire: PrestataireDetail
  displayMode: DisplayMode
}>()

const { prestataire } = usePrestataire()
const isEdit = computed(() => props.displayMode === 'edit')
const heroboardOpen = ref(false)
const hasMedia = computed(() => props.prestataire.medias.length > 0)

const emit = defineEmits<{
  openVideo: [youtubeId: string]
  openPhoto: [index: number]
  back: []
}>()

// ─── Carousel mobile ──────────────────────────────────────────────────────────
type HeroImage = { type: 'image'; src: string }
type HeroVideo = { type: 'video'; youtubeId: string }
type HeroItem = HeroImage | HeroVideo

const heroItems = computed<HeroItem[]>(() =>
  props.prestataire.medias.map((m) =>
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

const imageMedias = computed(() => props.prestataire.medias.filter((m) => m.type === 'IMAGE'))

const mosaicThumbs = computed<MosaicThumb[]>(() =>
  props.prestataire.medias.slice(1).map((m) => {
    if (m.type === 'YOUTUBE') {
      return { type: 'video', src: youtubeThumbnailUrl(m.ref), youtubeId: m.ref }
    }
    const photoIndex = imageMedias.value.findIndex((img) => img.position === m.position)
    return { type: 'photo', src: m.ref, photoIndex: photoIndex === -1 ? 0 : photoIndex }
  }),
)

function onThumbClick(thumb: MosaicThumb): void {
  if (thumb.type === 'video') {
    emit('openVideo', thumb.youtubeId)
  } else {
    emit('openPhoto', thumb.photoIndex)
  }
}

// ─── Share ────────────────────────────────────────────────────────────────────
async function share() {
  if (navigator.share) {
    await navigator.share({
      title: props.prestataire.name,
      text: t('prestataire.share-text', { name: props.prestataire.name }),
      url: window.location.href,
    })
  } else {
    await navigator.clipboard.writeText(window.location.href)
  }
}
</script>

<template>
  <section
    class="hero"
    :class="{ 'no-media': !hasMedia }"
    @touchstart.passive="onTouchStart"
    @touchend.passive="onTouchEnd"
  >
    <template v-if="hasMedia">
      <!-- ── Mobile : carousel ── -->
      <div class="carousel">
        <div v-if="heroItems[heroIndex]?.type === 'image'" class="image">
          <SgiltImage
            :src="(heroItems[heroIndex] as HeroImage).src"
            :alt="prestataire?.name"
            loading="eager"
          />
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

        <div class="overlay" aria-hidden="true" />

        <div class="content">
          <p class="category">{{ prestataire?.category }}</p>
          <h1 class="name">{{ prestataire?.name }}</h1>
          <EditableText
            as="p"
            v-model="prestataire!.baseline"
            field="baseline"
            :editable="isEdit"
            class="baseline"
          />
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
          <SgiltImage
            :src="prestataire?.medias ? (heroRef(prestataire.medias) ?? '') : ''"
            :alt="prestataire?.name"
            loading="eager"
          />
          <div class="overlay" aria-hidden="true" />
          <div class="content">
            <p class="category">{{ prestataire?.category }}</p>
            <h1 class="name">{{ prestataire?.name }}</h1>
            <EditableText
              as="p"
              v-model="prestataire!.baseline"
              field="baseline"
              :editable="isEdit"
              class="baseline"
            />
          </div>
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
        <p class="category">{{ prestataire?.category }}</p>
        <h1 class="name">{{ prestataire?.name }}</h1>
        <EditableText
          as="p"
          v-model="prestataire!.baseline"
          field="baseline"
          :editable="isEdit"
          class="baseline"
        />
      </div>

      <button v-if="isEdit" class="add-media-cta" type="button" @click="heroboardOpen = true">
        <ImageAddIcon />
        {{ $t('prestataire.add-medias-btn') }}
      </button>
    </div>

    <!-- Bouton back (masqué en mode édition) -->
    <button v-if="!isEdit" class="back" @click="emit('back')" aria-label="Retour">
      <ArrowLeftIcon />
    </button>

    <button v-if="!isEdit && hasMedia" class="share" @click="share" aria-label="Partager">
      <ShareIcon />
    </button>

    <button
      v-if="isEdit && hasMedia"
      class="edit-medias"
      type="button"
      @click="heroboardOpen = true"
    >
      <ImageAddIcon />
      {{ $t('prestataire.edit-medias-btn') }}
    </button>

    <PrestataireMediaDialog
      v-if="prestataire"
      v-model:open="heroboardOpen"
      :prestataire="prestataire"
    />
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

    .content {
      position: static;
      padding: 0;
      color: $text-primary;
      text-shadow: none;
    }

    .category {
      color: $brand-accent;
    }

    .name {
      color: $brand-primary;
    }

    .baseline {
      color: $text-secondary;
    }
  }

  .add-media-cta {
    display: inline-flex;
    align-items: center;
    gap: $spacing-xs;
    padding: $spacing-s $spacing-l;
    background: $brand-primary;
    color: $text-inverted;
    border: none;
    border-radius: $border-radius-xxl;
    font-family: inherit;
    font-size: $font-size-md;
    font-weight: $font-weight-semibold;
    cursor: pointer;
    transition: opacity 150ms ease;

    &:hover {
      opacity: 0.85;
    }

    svg {
      width: 1.1rem;
      height: 1.1rem;
      flex-shrink: 0;
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

  // ─── Bouton édition médias ────────────────────────────────────────────────
  .edit-medias {
    position: absolute;
    top: $spacing-m;
    right: $spacing-m;
    z-index: 10;
    display: flex;
    align-items: center;
    gap: 6px;
    padding: $spacing-xs $spacing-m;
    background: rgba(0, 0, 0, 0.5);
    border: 1px solid rgba(255, 255, 255, 0.4);
    backdrop-filter: blur(6px);
    border-radius: 2rem;
    color: #fff;
    font-family: inherit;
    font-size: 0.8rem;
    font-weight: 500;
    white-space: nowrap;
    cursor: pointer;
    transition: background 150ms ease;

    &:hover,
    &:active {
      background: rgba(0, 0, 0, 0.7);
    }

    svg {
      width: 14px;
      height: 14px;
      flex-shrink: 0;
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
