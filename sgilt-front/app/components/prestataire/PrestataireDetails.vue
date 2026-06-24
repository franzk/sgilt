<template>
  <div class="provider-page">
    <!-- ── Hero ─────────────────────────────────────────────────────────────── -->
    <PrestataireHero
      :prestataire="prestataire"
      :display-mode="displayMode"
      @open-video="openVideo"
      @open-photo="openGallery"
      @back="$emit('back')"
    />

    <!-- ── Layout principal ─────────────────────────────────────────────────── -->
    <div class="page-layout">
      <!-- Sidebar : datepicker + tarifs + CTA -->
      <aside ref="datepickerAreaRef" class="sidebar">
        <PrestataireSidebar
          :prestataire="prestataire"
          :display-mode="displayMode"
          :disable-date="disableDate"
          :date-error="dateError"
          @select-intent="onSelect"
        />
      </aside>

      <!-- Contenu principal -->
      <div class="main">
        <PrestataireContent :display-mode="displayMode" />
      </div>
    </div>

    <!-- ── Sticky CTA (mobile) ───────────────────────────────────────────────── -->
    <div class="sticky-cta">
      <SgiltButton v-if="displayMode === 'display'" @click="onSelect">{{ $t('provider.details.send-request') }}</SgiltButton>
      <EditActionsBar v-else />
    </div>

    <!-- ── Modale galerie ────────────────────────────────────────────────────── -->
    <Teleport to="body">
      <Transition name="modal">
        <div
          v-if="showGallery"
          class="modal-overlay"
          role="dialog"
          aria-modal="true"
          aria-label="Galerie photos"
          @click.self="closeGallery"
        >
          <button class="modal-close" aria-label="Fermer" @click="closeGallery">✕</button>
          <div class="gallery">
            <button class="nav prev" aria-label="Photo précédente" @click="prevPhoto">‹</button>
            <img
              :src="galleryPhotos[galleryIndex]"
              :alt="`Photo ${galleryIndex + 1}`"
              class="image"
            />
            <button class="nav next" aria-label="Photo suivante" @click="nextPhoto">›</button>
            <div class="counter">{{ galleryIndex + 1 }} / {{ galleryPhotos.length }}</div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- ── Modale vidéo ──────────────────────────────────────────────────────── -->
    <Teleport to="body">
      <Transition name="modal">
        <div
          v-if="showVideo"
          class="modal-overlay dark"
          role="dialog"
          aria-modal="true"
          aria-label="Vidéo"
          @click.self="closeVideo"
        >
          <button class="modal-close" aria-label="Fermer" @click="closeVideo">✕</button>
          <div class="video-wrapper">
            <iframe
              :src="`https://www.youtube.com/embed/${prestataire.youtubeId}?autoplay=1`"
              allow="
                accelerometer;
                autoplay;
                clipboard-write;
                encrypted-media;
                gyroscope;
                picture-in-picture;
              "
              allowfullscreen
              class="video-frame"
              title="Vidéo du prestataire"
            />
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import SgiltButton from '~/components/basics/buttons/SgiltButton.vue'
import PrestataireHero from '~/components/prestataire/PrestataireHero.vue'
import PrestataireContent from '~/components/prestataire/PrestataireContent.vue'
import EditActionsBar from '~/components/prestataire/EditActionsBar.vue'
import type { PrestataireDetail } from '~/data/prestataire/domain/PrestataireDetail'
import type { DisplayMode } from '~/types/prestataire'

const { t } = useI18n()

const props = withDefaults(defineProps<{
  prestataire: PrestataireDetail
  disableDate?: boolean
  displayMode?: DisplayMode
}>(), {
  displayMode: 'display',
})

const emit = defineEmits<{
  back: []
  select: [prestataire: PrestataireDetail]
}>()

// ── Soumission ────────────────────────────────────────────────────────────────
const { dateModel } = useSearchUi()
const dateError = ref<string | null>(null)
const datepickerAreaRef = ref<HTMLElement | null>(null)

watch(dateModel, () => {
  dateError.value = null
})

function onSelect() {
  if (!props.disableDate && !dateModel.value) {
    dateError.value = t('provider.details.date-required')
    // scroll vers le champ en erreur
    const el = datepickerAreaRef.value
    if (el) {
      const { top, bottom } = el.getBoundingClientRect()
      if (top < 0 || bottom > window.innerHeight) {
        el.scrollIntoView({ behavior: 'smooth', block: 'center' })
      }
    }
    return
  }
  emit('select', props.prestataire)
}

// ── Galerie ───────────────────────────────────────────────────────────────────
const showGallery = ref(false)
const galleryIndex = ref(0)

const { toUrl } = useImageUrl()

const galleryPhotos = computed<string[]>(() =>
  [props.prestataire.heroImage, ...props.prestataire.photos].map(toUrl),
)

function openGallery(index: number) {
  galleryIndex.value = index
  showGallery.value = true
}
function closeGallery() {
  showGallery.value = false
}
function prevPhoto() {
  galleryIndex.value =
    (galleryIndex.value - 1 + galleryPhotos.value.length) % galleryPhotos.value.length
}
function nextPhoto() {
  galleryIndex.value = (galleryIndex.value + 1) % galleryPhotos.value.length
}

// ── Vidéo ─────────────────────────────────────────────────────────────────────
const showVideo = ref(false)
function openVideo() {
  showVideo.value = true
}
function closeVideo() {
  showVideo.value = false
}

// ── Keyboard escape ───────────────────────────────────────────────────────────
function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Escape') {
    closeGallery()
    closeVideo()
  }
}
onMounted(() => window.addEventListener('keydown', onKeydown))
onUnmounted(() => window.removeEventListener('keydown', onKeydown))
</script>

<style scoped lang="scss">
@use '@/assets/styles/base' as *;

// ── Back button ───────────────────────────────────────────────────────────────
.back-btn {
  position: sticky;
  top: 0;
  z-index: 5;
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: $spacing-s $spacing-m;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(8px);
  border: none;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  font-family: inherit;
  font-size: 0.875rem;
  font-weight: 500;
  color: $text-secondary;
  cursor: pointer;
  width: 100%;
  text-align: left;
  transition: color 150ms ease;

  &:hover {
    color: $text-primary;
  }
}

// ── Page ──────────────────────────────────────────────────────────────────────
.provider-page {
  display: flex;
  flex-direction: column;
  min-height: 100dvh;
  padding-bottom: 6rem;

  @media (min-width: $breakpoint-desktop) {
    padding-bottom: 0;
  }
}

// ── Layout deux colonnes ──────────────────────────────────────────────────────
.page-layout {
  display: flex;
  flex-direction: column;

  @media (min-width: $breakpoint-desktop) {
    display: grid;
    grid-template-columns: 1fr 360px;
    grid-template-areas: 'main sidebar';
    max-width: 1280px;
    margin: 0 auto;
    gap: 3rem;
    padding: 2.5rem;
    align-items: start;
    width: 100%;
  }

  .main {
    @media (min-width: $breakpoint-desktop) {
      grid-area: main;
    }
  }

  .sidebar {
    @media (min-width: $breakpoint-desktop) {
      grid-area: sidebar;
      position: sticky;
      top: 5rem;
      background: #fff;
      border: 1px solid rgba(0, 0, 0, 0.08);
      border-radius: $radius-md;
      box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
    }
  }
}

// ── Sticky CTA (mobile) ───────────────────────────────────────────────────────
.sticky-cta {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 20;
  padding: $spacing-s $spacing-m;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: center;

  @media (min-width: $breakpoint-desktop) {
    display: none;
  }
}

// ── Modales ───────────────────────────────────────────────────────────────────
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.96);
  display: flex;
  align-items: center;
  justify-content: center;

  &.dark {
    background: rgba(0, 0, 0, 0.92);
  }
}

.modal-close {
  position: fixed;
  top: $spacing-m;
  right: $spacing-m;
  z-index: 101;
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  border: 1px solid rgba(0, 0, 0, 0.15);
  background: #fff;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: background 150ms ease;

  &:hover {
    background: #f5f5f5;
  }
}

.gallery {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;

  .image {
    max-width: 100%;
    max-height: 85vh;
    object-fit: contain;
    border-radius: $radius-md;
    box-shadow: 0 8px 40px rgba(0, 0, 0, 0.15);
  }

  .nav {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    width: 3rem;
    height: 3rem;
    border-radius: 50%;
    border: 1px solid rgba(0, 0, 0, 0.12);
    background: #fff;
    font-size: 1.4rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: background 150ms ease;

    &:hover {
      background: #f5f5f5;
    }

    &.prev {
      left: $spacing-m;
    }
    &.next {
      right: $spacing-m;
    }
  }

  .counter {
    position: absolute;
    bottom: $spacing-m;
    left: 50%;
    transform: translateX(-50%);
    font-size: 0.8rem;
    color: $text-secondary;
    background: rgba(255, 255, 255, 0.85);
    padding: 0.2rem 0.6rem;
    border-radius: 1rem;
  }
}

.video-wrapper {
  width: min(90vw, 900px);
  aspect-ratio: 16 / 9;
}

.video-frame {
  width: 100%;
  height: 100%;
  border: none;
  border-radius: $radius-md;
}

// ── Transitions ───────────────────────────────────────────────────────────────
.modal-enter-active,
.modal-leave-active {
  transition: opacity 250ms ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
